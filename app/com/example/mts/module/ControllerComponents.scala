package com.example.mts.module

import com.example.mts.controllers.{AccountManagementController, MoneyTransferController}
import com.softwaremill.macwire._
import play.api.mvc.{ControllerComponents => PlayControllerComponents}

trait ControllerComponents
  extends BaseComponents {

  def controllerComponents: PlayControllerComponents

  final lazy val accountManagementController: AccountManagementController =
    wire[AccountManagementController]

  final lazy val moneyTransferController: MoneyTransferController =
    wire[MoneyTransferController]
}