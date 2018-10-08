package com.example.mts.controllers

import com.example.mts.model.entity.Account
import com.example.mts.model.service.AccountService
import com.example.mts.util.{Jsons, Logging}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

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

}

object AccountManagementController {
  implicit val accountOutFormat: OFormat[Account] =
    Json.format[Account]
}
