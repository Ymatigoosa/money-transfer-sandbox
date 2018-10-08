package com.example.mts.module

import play.api.db.slick.{DbName, SlickComponents}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DatabaseComponents extends BaseComponents with SlickComponents {
  final lazy val dbConfig: DatabaseConfig[JdbcProfile] =
    slickApi.dbConfig[JdbcProfile](DbName("default"))
}
