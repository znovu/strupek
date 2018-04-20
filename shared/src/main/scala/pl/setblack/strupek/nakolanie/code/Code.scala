package pl.setblack.strupek.nakolanie.code
import upickle.default.{macroRW, ReadWriter => RW}

object Code {

  case class Project(compilationType: String, files: Seq[CompilationFile])

  case class CompilationFile(path: String)

  case class FileContents(default: String, alternatives: Map[String, String])

  private implicit def rwCompilationFile: RW[CompilationFile] = macroRW

  private implicit def rwProject: RW[Project] = macroRW

  val readProjectFromString = (str: String) => {
    import upickle.default._
    read[Project](str)
  }
}
