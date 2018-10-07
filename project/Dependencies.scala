import sbt._

object Dependencies {
  def core = Seq(
    Library.macwire % "provided",
    Library.h2,
    Library.playslick,
    Library.scalatestplusplay % Test
  )
}

object Version {
  val play = _root_.play.core.PlayVersion.current
}

object Library {
  val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.1"
  val playslick = "com.typesafe.play" %% "play-slick" % "3.0.1" // provides slick 3.2
  val h2 = "com.h2database" % "h2" % "1.4.197"
  val scalatestplusplay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"
}
