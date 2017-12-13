lazy val freesV = "0.4.6"

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "io.frees" %% "frees-core" % freesV,
    "io.frees" %% "frees-rpc" % "0.4.1",
    "org.scalameta" %% "scalameta" % "1.8.0"),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in(Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
)

// Common module:
lazy val `functional-microservices` = project
  .in(file("."))
  .settings(name := "functional-microservices")
  .settings(moduleName := "functional-microservices")
  .settings(description := "Freestyle at Scala eXchange")
  .settings(commonSettings)

// Data Generator:
lazy val `data-generator` =
  project.in(file("data-generator"))
    .settings(moduleName := "data-generator")
    .settings(commonSettings)
    .aggregate(`functional-microservices`)
    .dependsOn(`functional-microservices`)
    .settings(
      libraryDependencies ++= Seq(
        "joda-time" % "joda-time" % "2.9.9",
        "io.monix" %% "monix" % "3.0.0-M2",
        "org.scalacheck" %% "scalacheck" % "1.13.4",
        "com.47deg" %% "scalacheck-toolbox-datetime" % "0.2.3"
      )
    )

// RPC definitions and implementations:
lazy val services = project
  .in(file("services"))
  .settings(moduleName := "rpc-services")
  .settings(commonSettings)
  .aggregate(`functional-microservices`)
  .dependsOn(`functional-microservices`)

// Our application where we will test everything we are building:
lazy val app = project
  .in(file("app"))
  .settings(moduleName := "app")
  .settings(commonSettings)
  .aggregate(`data-generator`, services)
  .dependsOn(`data-generator`, services)
