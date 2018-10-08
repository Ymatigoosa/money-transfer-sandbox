package com.example.mts.model.dao

import akka.Done
import com.example.mts.model.entity.Account
import com.example.mts.util.Logging
import play.api.db.slick.HasDatabaseConfig
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** DAO for managing account information */
trait AccountDAO {

  /** Retrieve an account from the id. */
  def findById(id: String): Future[Option[Account]]

  /** Retrieve an account from the id. */
  def list(offset: Int, limit: Int): Future[Seq[Account]]

  /** create new account */
  def create(account: Account): Future[Int]

  /**
    * Performs money transfer from `idFrom` account to `idTo` account
    * @param idFrom    id of account from which we are transfering money
    * @param idTo      id of account to which we are transfering money
    * @param amount    amount of money
    * @param timestamp timestamp of operation
    * @throws IllegalStateException  when invariant checks against db is failed
    * @throws NoSuchElementException when one of elements not found
    * @return
    */
  def transferMoney(idFrom: String, idTo: String, amount: BigDecimal, timestamp: Long): Future[(Int, Int)]
}

/** internal methods for managing accounts table */
trait AccountDAOMaintenance {
  def createTables(): Future[Done]

  def dropTables(): Future[Done]
}

final class AccountDAOImpl(
  protected override val dbConfig: DatabaseConfig[JdbcProfile]
)(implicit ec: ExecutionContext)
  extends HasDatabaseConfig[JdbcProfile]
    with AccountDAO
    with AccountDAOMaintenance
    with Logging {

  import profile.api._

  class AccountTable(tag: Tag) extends Table[Account](tag, "ACCOUNT") {

    def id = column[String]("ID", O.PrimaryKey)

    def balance = column[BigDecimal]("BALANCE")

    def createdAt = column[Long]("CREATEDAT")

    def updatedAt = column[Long]("UPDATEDAT")

    def * = (id, balance, createdAt, updatedAt) <> (Account.tupled, Account.unapply _)
  }

  private val accounts = TableQuery[AccountTable]

  /** @inheritdoc */
  override def findById(id: String): Future[Option[Account]] =
    db.run(accounts.filter(_.id === id).result.headOption)

  /** @inheritdoc */
  override def list(offset: Int, limit: Int): Future[Seq[Account]] =
    db.run(accounts.drop(offset).take(limit).result)

  /** @inheritdoc */
  override def create(account: Account): Future[Int] = {
    db.run(accounts += account)
  }

  /** @inheritdoc */
  override def transferMoney(idFrom: String, idTo: String, amount: BigDecimal, timestamp: Long): Future[(Int, Int)] = {
    db.run(transferMoneyQuery(idFrom = idFrom, idTo = idTo, amount = amount, timestamp = timestamp).transactionally)
  }

  /** @inheritdoc */
  override def createTables(): Future[Done] = {
    logger.info("creating Account table...")
    val request = accounts.schema.create ::
      Nil

    safeRunSchemaAction(DBIO.sequence(request))
  }

  /** @inheritdoc */
  override def dropTables(): Future[Done] = {
    logger.info("dropping Account table...")
    val request = accounts.schema.drop ::
      Nil

    safeRunSchemaAction(DBIO.sequence(request))
  }

  /** run schema action with recover */
  private def safeRunSchemaAction[T](request: DBIOAction[T, NoStream, Effect.Schema]) = {
    db.run(request)
      .map(_ => Done)
      .recover {
        case NonFatal(e) =>
          logger.warn(s"schema action failed", e)
          Done
      }
  }

  private def transferMoneyQuery(idFrom: String, idTo: String, amount: BigDecimal, timestamp: Long) = {
    for {
      maybefrom <- accounts.filter(_.id === idFrom.bind).result.headOption
      maybeto <- accounts.filter(_.id === idTo.bind).result.headOption
      result <- checkAndTransfer(
        idFrom = idFrom,
        idTo = idTo,
        maybefrom = maybefrom,
        maybeto = maybeto,
        amount = amount,
        timestamp = timestamp
      )
    } yield result
  }

  private def checkAndTransfer(
    idFrom: String,
    idTo: String,
    maybefrom: Option[Account],
    maybeto: Option[Account],
    amount: BigDecimal,
    timestamp: Long
  ) = {
    (maybefrom, maybeto) match {
      case (Some(from), Some(to)) if from.balance >= amount =>
        doTransfer(from = from, to = to, amount = amount, timestamp = timestamp)

      case (Some(from), Some(_)) =>
        DBIO.failed(new IllegalStateException(s"not enough money in ${from.id} account"))

      case (None, None) =>
        DBIO.failed(new NoSuchElementException(s"accounts $idFrom and $idTo not found"))

      case (None, _) =>
        DBIO.failed(new NoSuchElementException(s"account $idFrom not found"))

      case (_, None) =>
        DBIO.failed(new NoSuchElementException(s"account $idTo not found"))
    }
  }

  private def doTransfer(from: Account, to: Account, amount: BigDecimal, timestamp: Long) = {
    for {
      updFrom <- accounts
        .filter(_.id === from.id.bind)
        .map(i => (i.balance, i.updatedAt))
        .update((from.balance - amount, timestamp))
      updTo <- accounts
        .filter(_.id === to.id.bind)
        .map(i => (i.balance, i.updatedAt))
        .update((to.balance + amount, timestamp))
    } yield (updFrom, updTo)
  }

}
