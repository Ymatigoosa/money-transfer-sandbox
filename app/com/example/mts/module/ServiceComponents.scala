package com.example.mts.module

import com.example.mts.model.dao.{AccountDAO, AccountDAOImpl, AccountDAOMaintenance}
import com.example.mts.model.service.{AccountService, AccountServiceImpl}
import com.softwaremill.macwire._

trait ServiceComponents extends BaseComponents {
  def accountDAOMaintenance: AccountDAOMaintenance
  def accountDAO: AccountDAO

  final lazy val accountService: AccountService = wire[AccountServiceImpl]
}
