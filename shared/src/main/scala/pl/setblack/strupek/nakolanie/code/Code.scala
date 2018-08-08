package pl.setblack.strupek.nakolanie.code
import upickle.default.{macroRW, ReadWriter => RW}

object Code {

  case class Project(compilationType: String, files: Seq[CompilationFile])

  case class CompilationFile(path: String)

  case class FileContents(default: String, alternatives: Map[String, String])

  import upickle.default._
  import JsonOps._
  val readProjectFromString = (str: String) => {
    read[Project](str)
  }

  val readContentsFromString = (str: String) => {
    read[CompilationFile](str)
  }

  def writeJson( prj : Project) : String = write(prj)

  def writeJson( prjs : Seq[Project]) : String = write(prjs)

  def writeJson( contents : FileContents ) : String = write(contents)

  object JsonOps {
    implicit def rwFileContents: RW[FileContents] = macroRW

    implicit def rwCompilationFile: RW[CompilationFile] = macroRW

    implicit def rwProject: RW[Project] = macroRW
  }

}
