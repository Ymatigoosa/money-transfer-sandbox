import sbt.Keys._
import sbt._

object Dependencies {
  val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided"
  val scalatestplusplay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
  
  def application = Seq(
    macwire
  )
  
  def test = Seq(
    scalatestplusplay
  )
}
