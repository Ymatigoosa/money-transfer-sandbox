import Dependencies._

name := """money-transfer-sandbox"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, JavaAppPackaging)

scalaVersion := "2.12.6"

scalacOptions ++= CompilerOptions.scalacOptions

libraryDependencies ++= Dependencies.core