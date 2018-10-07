package com.example.mts.module

import akka.actor.ActorSystem
import play.api._
import play.api.ApplicationLoader.Context
import com.softwaremill.macwire._
import router.Routes

final class MoneyTransferSandboxComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with NoHttpFiltersComponents
    with ControllerComponents
    with DatabaseComponents {

  implicit def as: ActorSystem = actorSystem

  // Router
  lazy val router: Routes = {
    val routePrefix: String = "/"
    wire[Routes]
  }
}