package com.example.mts.module

import com.example.mts.model.dao.{AccountDAO, AccountDAOImpl, AccountDAOMaintenance}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import com.softwaremill.macwire._

trait DAOComponents extends BaseComponents {
  def dbConfig: DatabaseConfig[JdbcProfile]

  private final lazy val accountDAOImpl: AccountDAOImpl = wire[AccountDAOImpl]
  final def accountDAOMaintenance: AccountDAOMaintenance = accountDAOImpl
  final def accountDAO: AccountDAO = accountDAOImpl
}
