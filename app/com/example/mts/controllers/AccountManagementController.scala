package com.example.mts.controllers

import com.example.mts.model.entity.Account
import com.example.mts.model.service.AccountService
import com.example.mts.util.{Jsons, Logging}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

/** Controller for managing crud operations for accounts */
final class AccountManagementController(
  override val controllerComponents: ControllerComponents,
  accountService: AccountService
)(implicit ec: ExecutionContext) extends BaseController with Logging {

  import AccountManagementController._

  /** Retrieve an account from the id. */
  def findById(id: String): Action[AnyContent] = Action.async {
    accountService.findById(id).map {
      case Some(account) =>
        Ok(Jsons.success(account))
      case None =>
        NotFound(Jsons.error(s"$id not found"))
    }
  }

  /** Retrieve an account from the id. */
  def list(offset: Option[Int], limit: Option[Int]): Action[AnyContent] = Action.async {
    accountService.list(
      offset = offset.filter(_ > 0).getOrElse(0),
      limit = limit.filter(_ > 0).getOrElse(10)
    ).map(seq => Ok(Jsons.success(seq)))
  }

  /** create new account */
  def create(id: String): Action[AnyContent] = Action.async {
    accountService.create(id).map {
      case Some(_) => Created
      case None => Ok
    }
  }

  /** create new account */
  def addMoney(): Action[AddMoneyRequest] = Action.async(parse.json[AddMoneyRequest]) { request =>
    val body = request.body
    accountService.addMoney(id = body.id, amount = body.amount).map {
      case Some(_) => Ok
      case None => NotFound
    }
  }

}

object AccountManagementController {
  implicit val formatAccountResponse: OFormat[Account] =
    Json.format[Account]

  case class AddMoneyRequest(id: String, amount: BigDecimal)
  implicit val formatAddMoneyRequest: OFormat[AddMoneyRequest] =
    Json.format[AddMoneyRequest]
}
