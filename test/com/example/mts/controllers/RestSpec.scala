package com.example.mts.controllers

import akka.Done
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.json
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class RestSpec
  extends PlaySpec
  with BaseOneAppPerTest
  with MyApplicationFactory
  with ScalaFutures {

  implicit def ec: ExecutionContext = app.actorSystem.dispatcher

  "get by id" should {
    "get existing account" in withSeed {
      val resp = route(app, FakeRequest(GET, "/api/account/1")).get

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      (contentAsJson(resp) \ "data" \ "id").as[String] mustBe "1"
    }

    "return not found" in withSeed {
      val resp = route(app, FakeRequest(GET, "/api/account/11111111")).get

      status(resp) mustBe NOT_FOUND
    }
  }

  "create account" should {
    "return 201 on successful creation" in withSeed {
      val resp = route(app, FakeRequest(PUT, "/api/account/3")).get

      status(resp) mustBe CREATED
    }

    "return 200 if account exists" in withSeed {
      val resp = route(app, FakeRequest(PUT, "/api/account/1")).get

      status(resp) mustBe OK
    }
  }

  "add balance" should {
    "return 200 on success" in withSeed {
      val resp = route(app, FakeRequest(POST, "/api/account/balance/add")
        .withBody(Json.obj("id" -> "1", "amount" -> 10))
      ).get

      status(resp) mustBe OK

      whenReady(resp) { _ =>
        val resp2 = route(app, FakeRequest(GET, "/api/account/1")).get
        status(resp2) mustBe OK
        contentType(resp2) mustBe Some("application/json")
        (contentAsJson(resp2) \ "data" \ "id").as[String] mustBe "1"
        (contentAsJson(resp2) \ "data" \ "balance").as[BigDecimal] mustBe BigDecimal(20)
      }
    }

    "return 404 if account exists" in withSeed {
      val resp = route(app, FakeRequest(POST, "/api/account/balance/add")
        .withBody(Json.obj("id" -> "1111111", "amount" -> 10))
      ).get

      status(resp) mustBe NOT_FOUND
    }
  }

  "money transfer" should {
    "return 200 on success" in withSeed {
      val fr = FakeRequest(POST, "/api/moneytransfer")
        .withBody(Json.obj("idFrom" -> "1", "idFrom" -> "2" ,"amount" -> 10))
      val resp = route(app, fr).get

      status(resp) mustBe OK
    }

    "return 404 if `From` account not exists" in withSeed {
      val fr = FakeRequest(POST, "/api/moneytransfer")
        .withBody(Json.obj("idFrom" -> "111111", "idFrom" -> "2" ,"amount" -> 10))
      val resp = route(app, fr).get

      status(resp) mustBe NOT_FOUND
    }

    "return 404 if `To` account not exists" in withSeed {
      val fr = FakeRequest(POST, "/api/moneytransfer")
        .withBody(Json.obj("idFrom" -> "1", "idFrom" -> "222222" ,"amount" -> 10))
      val resp = route(app, fr).get

      status(resp) mustBe NOT_FOUND
    }

    "return 404 if both accounts not exists" in withSeed {
      val fr = FakeRequest(POST, "/api/moneytransfer")
        .withBody(Json.obj("idFrom" -> "1", "idFrom" -> "222222" ,"amount" -> 10))
      val resp = route(app, fr).get

      status(resp) mustBe NOT_FOUND
    }

    "return 400 if `From` account has insufficient money to transfer`" in withSeed {
      val fr = FakeRequest(POST, "/api/moneytransfer")
        .withBody(Json.obj("idFrom" -> "1", "idFrom" -> "2" ,"amount" -> 11))
      val resp = route(app, fr).get

      status(resp) mustBe BAD_REQUEST
    }
  }

  def withSeed[T](body: => T) {
    val req = for {
      r1 <- route(app, FakeRequest(POST, "/maintenance/tables")).get
      r2 <- route(app, FakeRequest(PUT, "/api/account/1")).get
      r3 <- route(app, FakeRequest(PUT, "/api/account/2")).get
      r4 <- route(app, FakeRequest(POST, "/api/account/balance/add")
        .withBody(Json.obj("id" -> "1", "amount" -> 10))
      ).get
    } yield Done
    Await.result(req, 30.seconds)
  }
}
