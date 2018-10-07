package com.example.mts.module

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.{Configuration, Environment}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext

trait BaseComponents {
  implicit def materializer: Materializer
  implicit def executionContext: ExecutionContext
  implicit def as: ActorSystem
  def configuration: Configuration
  def environment: Environment
  def applicationLifecycle: ApplicationLifecycle
}
