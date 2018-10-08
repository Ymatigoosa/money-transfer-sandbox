package com.example.mts.util

import play.api.libs.json._

object Jsons {

  def success[T](o: T)(implicit tjs: Writes[T]): JsObject = Json.obj(
    "data" -> tjs.writes(o)
  )

  def error(body: JsValue): JsObject = Json.obj(
    "error" -> body
  )

  def error(msg: String): JsObject =
    error(JsString(msg))
}
