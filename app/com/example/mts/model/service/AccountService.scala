package com.example.mts.model.service

import com.example.mts.model.dao.AccountDAO
import com.example.mts.model.entity.Account
import com.example.mts.util.Logging

import scala.concurrent.{ExecutionContext, Future}

trait AccountService {

  import AccountService._

  /** Retrieve an account from the id. */
  def findById(id: String): Future[Option[Account]]

  /** Retrieve an account from the id. */
  def list(offset: Int, limit: Int): Future[Seq[Account]]

  /** create new account */
  def create(id: String): Future[Option[Account]]

  /**
    * Performs money transfer from `idFrom` account to `idTo` account
    * @param idFrom    id of account from which we are transfering money
    * @param idTo      id of account to which we are transfering money
    * @param amount    amount of money
    * @return
    */
  def transferMoney(idFrom: String, idTo: String, amount: BigDecimal): Future[MoneyTransferResult]

  /**
    * add money to `id` account
    * @return old account(!)
    **/
  def addMoney(id: String, amount: BigDecimal): Future[Option[Account]]
}

object AccountService {

  /** result of [[AccountService.transferMoney]] method */
  sealed trait MoneyTransferResult

  case class MoneyTransferError(msg: String) extends MoneyTransferResult

  case class MoneyTransferNotFound(msg: String) extends MoneyTransferResult

  case object MoneyTransferSuccess extends MoneyTransferResult

}

final class AccountServiceImpl(dao: AccountDAO)(implicit ec: ExecutionContext) extends AccountService with Logging {

  import AccountService._

  /** @inheritdoc */
  override  def findById(id: String): Future[Option[Account]] = {
    dao.findById(id)
  }

  /** @inheritdoc */
  override  def list(offset: Int, limit: Int): Future[Seq[Account]] = {
    dao.list(offset = offset, limit = limit)
  }

  /** @inheritdoc */
  override  def create(id: String): Future[Option[Account]] = {
    logger.info(s"creating new account with id=$id")
    dao.findById(id).flatMap {
      case Some(_) => // skip if already created
        logger.warn(s"account already exists id=$id")
        Future.successful(None)

      case None =>
        val timestamp: Long = System.currentTimeMillis()
        val account = Account(id, BigDecimal(0), timestamp, timestamp)
        dao.create(account).map {_ =>
          logger.info(s"created account $account")
          Some(account)
        }
    }

  }

  /** @inheritdoc */
  override  def transferMoney(
    idFrom: String,
    idTo: String,
    amount: BigDecimal
  ): Future[MoneyTransferResult] = {
    logger.info(s"transfering $amount money from $idFrom to $idTo")
    val timestamp: Long = System.currentTimeMillis()
    val result: Future[MoneyTransferResult] =
      dao.transferMoney(idFrom = idFrom, idTo = idTo, amount = amount, timestamp = timestamp)
      .map(_ => MoneyTransferSuccess)

    result.recover {
        case ex: IllegalStateException =>
          logger.warn("transfering money ended with error", ex)
          MoneyTransferError(ex.getMessage)

        case ex: NoSuchElementException =>
          logger.warn("transfering money ended with error", ex)
          MoneyTransferNotFound(ex.getMessage)
    }
  }

  /** @inheritdoc */
  override def addMoney(id: String, amount: BigDecimal): Future[Option[Account]] = {
    logger.info(s"add $amount money $id")
    val timestamp: Long = System.currentTimeMillis()
    dao.findById(id).flatMap {
      case Some(account) =>
        dao.addMoney(id, account.balance + amount, timestamp)
          .map(_ => Some(account)) // returning old account here

      case None =>
        Future.successful(None)
    }
  }
}
