lazy val derivable = applyDefaultSettings(project.in(file(".")))
  .aggregate(core, tests)

lazy val core = module("core")

lazy val tests = module("tests")
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core"    % "0.8.0",
      "io.circe" %% "circe-generic" % "0.8.0"
    )
  ).dependsOn(core)

lazy val benchmark = module("benchmark")
  .enablePlugins(JmhPlugin)
  .dependsOn(tests)

lazy val defaultScalacOptions = Seq(
  "-Xfatal-warnings",
  "-unchecked",
  "-feature",
  "-deprecation",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls"
)

lazy val defaultLibraryDependencies = Seq(
  "org.typelevel" %% "macro-compat" % "1.1.1"
) ++ defaultTestDependencies

lazy val macroSettings = Seq(
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-reflect" % scalaVersion.value % Provided,
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided
  ),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val defaultTestDependencies = Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.4",
  "org.scalatest" %% "scalatest" % "3.0.0",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.5"
).map(_ % "test")

def applyDefaultSettings(project: Project) = project.settings(
  scalacOptions ++= defaultScalacOptions,
  libraryDependencies ++= defaultLibraryDependencies ++ defaultTestDependencies,
  macroSettings
)

def module(path: String) = {
  // Convert path from lisp-case to camelCase
  val id = path.split("-").reduce(_ + _.capitalize)
  // Convert path from list-case to "space case"
  val docName = path.replace('-', ' ')
  // Set default and module-specific settings.
  applyDefaultSettings(Project(id, file(path))).settings(
    name := "Derivable " + docName,
    moduleName := "derivable-" + path,
    description := "derivable" + docName
  )
}
