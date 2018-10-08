package com.example.mts.controllers

import java.io.File

import com.example.mts.Loader
import org.scalatestplus.play.FakeApplicationFactory
import play.api._
import play.api.inject._
import play.core.DefaultWebCommands

trait MyApplicationFactory extends FakeApplicationFactory {

  override def fakeApplication: Application = {
    val env = Environment.simple(new File("."))
    val configuration = Configuration.load(env)
    val context = ApplicationLoader.Context(
      environment = env,
      sourceMapper = None,
      webCommands = new DefaultWebCommands(),
      initialConfiguration = configuration,
      lifecycle = new DefaultApplicationLifecycle()
    )
    val loader = new Loader()
    loader.load(context)
  }

}
