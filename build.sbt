name := """Weathers Better"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "de.grundid.opendatalab" % "geojson-jackson" % "1.1",
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)
