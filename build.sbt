lazy val freesV = "0.4.6"

lazy val `functional-microservices` = project
  .in(file("."))
  .settings(name := "functional-microservices")
  .settings(moduleName := "functional-microservices")
  .settings(description := "Freestyle at Scala eXchange")
  .settings(
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "io.frees" %% "frees-core" % freesV,
      "org.scalameta" %% "scalameta" % "1.8.0"
    ),
    scalacOptions += "-Xplugin-require:macroparadise",
    scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
  )
