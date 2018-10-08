package com.example.mts.controllers

import com.example.mts.model.service.AccountService
import com.example.mts.model.service.AccountService.{MoneyTransferError, MoneyTransferNotFound, MoneyTransferResult, MoneyTransferSuccess}
import com.example.mts.util.{Jsons, Logging}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

final class MoneyTransferController (
  override val controllerComponents: ControllerComponents,
  accountService: AccountService
)(implicit ec: ExecutionContext) extends BaseController with Logging {
  import MoneyTransferController._

  def transferMoney(): Action[MoneyTransferRequest] = Action.async(parse.json[MoneyTransferRequest]) { request =>
    val reqdata = request.body
    accountService.transferMoney(
      idFrom = reqdata.idFrom,
      idTo = reqdata.idTo,
      amount = reqdata.amount
    ).map {
      case MoneyTransferError(msg) =>
        BadRequest(Jsons.error(msg))

      case MoneyTransferNotFound(msg) =>
        NotFound(Jsons.error(msg))

      case MoneyTransferSuccess =>
        Ok
    }
  }

}

object MoneyTransferController {
  case class MoneyTransferRequest(
    idFrom: String,
    idTo: String,
    amount: BigDecimal
  )

  implicit val formatMoneyTransferRequest: OFormat[MoneyTransferRequest] =
    Json.format[MoneyTransferRequest]
}
