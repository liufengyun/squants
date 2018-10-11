import sbtcrossproject.{crossProject, CrossType}

lazy val defaultSettings =
  Project.defaultSettings ++
  Compiler.defaultSettings ++
  Publish.defaultSettings ++
  Tests.defaultSettings ++
  Formatting.defaultSettings ++
  Console.defaultSettings ++
  Docs.defaultSettings

lazy val squants = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .enablePlugins(TutPlugin)
  .in(file("."))
  .settings(defaultSettings: _*)
  .jvmSettings(
    osgiSettings,
    tutTargetDirectory := file("."),
    tutSourceDirectory := file("shared") / "src" / "main" / "tut"
  )
  .jsSettings(
    parallelExecution in Test := false,
    excludeFilter in Test := "*Serializer.scala" || "*SerializerSpec.scala"
  )

lazy val root = project.in(file("."))
  .settings(defaultSettings: _*)
  .settings(
    name := "squants",
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
  .aggregate(squantsJVM, squantsJS, squantsNative)

lazy val squantsJVM = squants.jvm.enablePlugins(SbtOsgi).settings(dottySettings)
lazy val squantsJS = squants.js
lazy val squantsNative = squants.native

lazy val dottySettings = List(
  scalaVersion := "0.11.0-bin-20181008-045f7f3-NIGHTLY",
  libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value)),
  scalacOptions := List("-language:Scala2,implicitConversions")
)
