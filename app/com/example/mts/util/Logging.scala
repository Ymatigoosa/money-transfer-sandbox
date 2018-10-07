package com.example.mts.util

import play.api.Logger

trait Logging {
  protected final val logger: Logger =
    Logger(this.getClass)
}
