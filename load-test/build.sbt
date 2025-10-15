enablePlugins(GatlingPlugin)

scalaVersion := "2.13.17"

val gatlingVersion = "3.14.6"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion % "test,it"
libraryDependencies += "com.lihaoyi" %% "upickle" % "4.3.2"