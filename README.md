# frees-workshop: Building Purely Functional Microservices

In this workshop you will learn how to build from scratch a purely functional application and expose it as a microservice with Freestyle and Freestyle RPC.

This will be a hands on coding session where we will architect a small application based on Algebras and Modules that can be exposed as an RPC microservice supporting Protobuf and Avro serialization protocols.

## Basic Freestyle Structure

We are going to use the [freestyle-seed](https://github.com/frees-io/freestyle-seed.g8) [giter8](https://github.com/foundweekends/giter8) template to create the basic project structure:

```bash
sbt new frees-io/freestyle-seed.g8
```

Result:

```bash
name [Project Name]: frees-rpc-workshop
projectDescription [Project Description]:  Freestyle at Scala eXchange
project [project-name]: functional-microservices
package [freestyle]: scalaexchange
freesVersion [0.4.6]:

Template applied in ./frees-rpc-workshop
```

Run the example:

```bash
cd frees-rpc-workshop
sbt run
```

## App Domain and Data Generator

In this section we'll see the different models that we will be using along the workshop.

Before that, let's add some new sbt settings (and sbt modules) to be able to put our code in the right place.

Add the following to your project's `build.sbt` file:

* Common Settings and dependency we'll use for every module in our project:

```scala
lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  resolvers += Resolver.bintrayRepo("beyondthelines", "maven"),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "io.frees" %% "frees-core" % freesV,
    "io.frees" %% "frees-rpc" % "0.4.1",
    "org.scalameta" %% "scalameta" % "1.8.0"),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in(Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
)
```

* Modify the current settings in our current `functional-microservices` sbt module, you can use the following configuration:

```scala
// Common module:
lazy val `functional-microservices` = project
  .in(file("."))
  .settings(name := "functional-microservices")
  .settings(moduleName := "functional-microservices")
  .settings(description := "Freestyle at Scala eXchange")
  .settings(commonSettings)
```

* Remove all the code provided by the giter8 template (you can just directly remove the `scalaexchange` folder/package), since we are building everything from scratch in the next steps. In fact, from now on, `functional-microservices` sbt module will be used as the common build, which it'll be visible for the rest of the sbt modules.

* Our model, that could be placed in the common space within our project ([./src/main/scala](./src/main/scala)):

  * [./src/main/scala/models.scala](./src/main/scala/models.scala):

```scala
package scalaexchange

sealed trait EventType
case object ProcessedCheckout   extends EventType
case object UnprocessedCheckout extends EventType
case object Login               extends EventType

case class UserEvent(userId: Int, eventType: EventType, date: String)
```

  * [./src/main/scala/errors.scala](./src/main/scala/errors.scala):

```scala
package scalaexchange

case class DataGenerationException(message: String, maybeCause: Option[Throwable] = None)
  extends RuntimeException(message) {

  maybeCause foreach initCause
}
```

* Now, let's define three new `sbt` modules, also in our `build.sbt`, at once that will be needed later on:

```scala
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
```

* Streaming Data Generation: you can copy this code verbatim from this path [./data-generator/src/main/scala/](./data-generator/src/main/scala/) inside the `data-generator` sbt module folder, given this code is out of the scope of this workshop. Notice that this should be under the `data-generator` folder.

* Checkpoint: let's test this streaming data generation before moving onto the next section. We create the `App` class inside the `app` sbt module folder([./app/src/main/scala/App.scala](./app/src/main/scala/App.scala)). Notice that this should be under the `app` folder.

```scala
package scalaexchange

import monix.execution.Scheduler

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object App {

  implicit val S: Scheduler = monix.execution.Scheduler.Implicits.global

  def main(args: Array[String]): Unit = {
    val streamingService: StreamingService = new StreamingService

    Await.ready(streamingService.userEventsStream.completedL.runAsync, Duration.Inf)
  }

}
```

Now you can run this application:

```bash
sbt app/run
```

The output should be similar to:

```bash
[info] Running scalaexchange.App
* New Event 👍  --> UserEvent(76,ProcessedCheckout,2017-12-14T15:25:48.820Z)
* New Event 👍  --> UserEvent(21,UnprocessedCheckout,2017-12-05T05:08:20.558Z)
* New Event 👍  --> UserEvent(16,ProcessedCheckout,2017-12-07T11:35:20.559Z)
* New Event 👍  --> UserEvent(80,UnprocessedCheckout,2017-12-07T17:32:24.181Z)
* New Event 👍  --> UserEvent(28,Login,2017-12-08T14:50:48.704Z)
* New Event 👍  --> UserEvent(63,UnprocessedCheckout,2017-12-03T06:22:30.471Z)
* New Event 👍  --> UserEvent(94,ProcessedCheckout,2017-12-08T08:59:24.241Z)
* New Event 👍  --> UserEvent(74,Login,2017-12-12T08:03:18.996Z)
* New Event 👍  --> UserEvent(43,UnprocessedCheckout,2017-12-12T03:16:03.142Z)
* New Event 👍  --> UserEvent(32,ProcessedCheckout,2017-12-09T10:09:40.566Z)
* New Event 👍  --> UserEvent(74,UnprocessedCheckout,2017-12-05T20:39:46.105Z)
* New Event 👍  --> UserEvent(74,ProcessedCheckout,2017-12-07T03:36:50.931Z)

... // Feel free to stop it, it has no end.
```

## Freestyle RPC - Protocols and Services
