import sbt._
import Keys._

object BuildHelper {

  lazy val commonSettings = Seq(
    // Refine scalac params from tpolecat
    scalacOptions --= Seq(
      "-Xfatal-warnings"
    ),
    scalacOptions ++= Seq("-language:higherKinds")
  )

  def isOldScala(sv: String): Boolean =
    CrossVersion.partialVersion(sv) match {
      case Some((2, minor)) if minor < 13 => true
      case _                              => false
    }

  val macroSettings: Seq[Setting[_]] = {

    def paradiseDependency(sv: String): Seq[ModuleID] =
      if (isOldScala(sv))
        Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch))
      else
        Seq.empty

    def macroAnnotationScalacOption(sv: String): Seq[String] =
      if (isOldScala(sv))
        Seq.empty
      else
        Seq("-Ymacro-annotations")

    Seq(
      libraryDependencies ++= paradiseDependency(scalaVersion.value),
      scalacOptions ++= macroAnnotationScalacOption(scalaVersion.value)
    )
  }

}
