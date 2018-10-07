package com.example.mts.module

import akka.actor.ActorSystem
import com.example.mts.util.ErrorHandler
import play.api._
import play.api.ApplicationLoader.Context
import com.softwaremill.macwire._
import play.api.http.HttpErrorHandler
import router.Routes

final class MoneyTransferSandboxComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with NoHttpFiltersComponents
    with ControllerComponents
    with DatabaseComponents {

  override lazy val httpErrorHandler: HttpErrorHandler = wire[ErrorHandler]

  implicit def as: ActorSystem = actorSystem

  // Router
  lazy val router: Routes = {
    val routePrefix: String = "/"
    wire[Routes]
  }
}