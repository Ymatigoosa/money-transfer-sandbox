package com.example.mts.util

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.routing.Router
import play.core.SourceMapper

import scala.concurrent._

/**
  * Overrides default exception handling behavior
  * Instead of sending an error page json obj will be sent
  */
final class ErrorHandler (
  env: Environment,
  config: Configuration,
  sourceMapper: Option[SourceMapper],
  router: => Router
) extends DefaultHttpErrorHandler(env, config, sourceMapper, Some(router)) with Logging {

  /** @inheritdoc */
  override def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    Future.successful {
      Results.BadRequest(Jsons.error(message))
    }
  }

  /** @inheritdoc */
  override def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    Future.successful {
      Results.Forbidden(Jsons.error(message))
    }
  }

  /** @inheritdoc */
  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful {
      Results.NotFound(Jsons.error(message))
    }
  }

  /** @inheritdoc */
  override def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful {
      Results.Status(statusCode)(Jsons.error(message))
    }
  }

  /** @inheritdoc */
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    log(exception)
    Future.successful {
      Results.InternalServerError(Jsons.error(exception.getLocalizedMessage))
    }
  }

  private def log(exception: Throwable): Unit = {
    logger.error("core onServerError handled exception", exception)
  }
}
