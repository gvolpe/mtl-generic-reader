import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._
import com.scalapenos.sbt.prompt._
import Dependencies._

name := """mtl-generic-reader"""

organization in ThisBuild := "io.github.gvolpe"

crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.8")

sonatypeProfileName := "com.github.gvolpe"

promptTheme := PromptTheme(
  List(
    text("[sbt] ", fg(105)),
    text(_ => "mtl-gen-reader", fg(15)).padRight(" λ ")
  )
)

val commonSettings = Seq(
	organizationName := "Gabriel Volpe",
	startYear := Some(2019),
	licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
	homepage := Some(url("https://github.com/gvolpe/mtl-generic-reader/")),
	headerLicense := Some(HeaderLicense.ALv2("2019", "Gabriel Volpe")),
	libraryDependencies ++= Seq(
		compilerPlugin(Libraries.kindProjector),
		compilerPlugin(Libraries.betterMonadicFor),
		compilerPlugin(Libraries.macroParadise)
	),
	resolvers += "Apache public" at "https://repository.apache.org/content/groups/public/",
	scalafmtOnCompile := true,
	publishTo := {
		val sonatype = "https://oss.sonatype.org/"
		if (isSnapshot.value)
			Some("snapshots" at sonatype + "content/repositories/snapshots")
		else
			Some("releases" at sonatype + "service/local/staging/deploy/maven2")
	},
	publishMavenStyle := true,
	publishArtifact in Test := false,
	pomIncludeRepository := { _ => false },
	pomExtra :=
			<developers>
				<developer>
					<id>gvolpe</id>
					<name>Gabriel Volpe</name>
					<url>http://github.com/gvolpe</url>
				</developer>
			</developers>
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

lazy val CoreDependencies = Seq(
	Libraries.cats,
	Libraries.catsMtl
)

lazy val LawsDependencies = Seq(
	Libraries.catsEffectLaws
)

lazy val CatsDependencies = Seq(
	Libraries.scalaTest      % "test",
	Libraries.scalaCheck     % "test",
  Libraries.catsTestKit % Test
)

lazy val ZioDependencies = Seq(
	Libraries.zioCore,
	Libraries.zioCats,
	Libraries.scalaTest      % "test",
	Libraries.scalaCheck     % "test",
  Libraries.catsTestKit % Test
)

lazy val ExamplesDependencies = Seq(
	Libraries.catsMeowMtl,
	Libraries.catsEffect
)

lazy val `mtl-gen-reader-root` = project.in(file("."))
  .aggregate(`mtl-gen-reader-core`, `mtl-gen-reader-laws`, `mtl-gen-reader-cats`, `mtl-gen-reader-zio`, examples)
  .settings(noPublish)

lazy val `mtl-gen-reader-core` = project.in(file("modules/core"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= CoreDependencies)
  .settings(parallelExecution in Test := false)
  .enablePlugins(AutomateHeaderPlugin)

lazy val `mtl-gen-reader-laws` = project.in(file("modules/laws"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= LawsDependencies)
  .settings(parallelExecution in Test := false)
  .dependsOn(`mtl-gen-reader-core`)
  .enablePlugins(AutomateHeaderPlugin)

lazy val `mtl-gen-reader-cats` = project.in(file("modules/cats"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= CatsDependencies)
  .settings(parallelExecution in Test := false)
  .dependsOn(`mtl-gen-reader-core`, `mtl-gen-reader-laws`)
  .enablePlugins(AutomateHeaderPlugin)

lazy val `mtl-gen-reader-zio` = project.in(file("modules/zio"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= ZioDependencies)
  .settings(parallelExecution in Test := false)
  .dependsOn(`mtl-gen-reader-core`, `mtl-gen-reader-laws`)
  .enablePlugins(AutomateHeaderPlugin)

lazy val examples = project.in(file("modules/examples"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= ExamplesDependencies)
  .settings(parallelExecution in Test := false)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(`mtl-gen-reader-cats`, `mtl-gen-reader-zio`)
  .settings(noPublish)

