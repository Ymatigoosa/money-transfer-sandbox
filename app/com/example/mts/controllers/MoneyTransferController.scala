package com.example.mts.controllers

import com.example.mts.util.Logging
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

final class MoneyTransferController (
  override val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController with Logging {

  def test(): Action[AnyContent] = Action {
    Ok(Json.obj("data" -> 1))
  }

}
