package com.example.mts.module

import com.example.mts.controllers.{AccountManagementController, MaintenanceController, MoneyTransferController}
import com.example.mts.model.dao.AccountDAOMaintenance
import com.example.mts.model.service.AccountService
import com.softwaremill.macwire._
import play.api.mvc.{ControllerComponents => PlayControllerComponents}

trait ControllerComponents
  extends BaseComponents {

  def controllerComponents: PlayControllerComponents

  def accountService: AccountService
  def accountDAOMaintenance: AccountDAOMaintenance

  final lazy val accountManagementController: AccountManagementController =
    wire[AccountManagementController]

  final lazy val moneyTransferController: MoneyTransferController =
    wire[MoneyTransferController]

  final lazy val maintenanceController: MaintenanceController =
    wire[MaintenanceController]
}