package com.example.mts

import com.example.mts.module.MoneyTransferSandboxComponents
import play.api.{Application, ApplicationLoader, LoggerConfigurator}
import play.api.ApplicationLoader.Context

final class Loader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
    val components = new MoneyTransferSandboxComponents(context)

    components.application
  }
}
