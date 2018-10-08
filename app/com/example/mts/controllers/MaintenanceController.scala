package com.example.mts.controllers

import com.example.mts.model.dao.AccountDAOMaintenance
import com.example.mts.util.Logging
import play.api.mvc._

import scala.concurrent.ExecutionContext

/** Controller for managing internal database tasks */
final class MaintenanceController (
  override val controllerComponents: ControllerComponents,
  accountDAOMaintenance: AccountDAOMaintenance
)(implicit ec: ExecutionContext) extends BaseController with Logging {

  def createTables(): Action[AnyContent] = Action.async {
    accountDAOMaintenance.createTables().map { _ =>
      Ok
    }
  }

  def dropTables(): Action[AnyContent] = Action.async {
    accountDAOMaintenance.dropTables().map { _ =>
      Ok
    }
  }

}
