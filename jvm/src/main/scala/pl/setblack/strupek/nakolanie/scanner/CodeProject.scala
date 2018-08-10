package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.{Files, Path}

import pl.setblack.strupek.nakolanie.code.Code._
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.scanner.CodeModule.{CodeModule, CopyChance}
import scalaz.concurrent.Task
import scalaz.{-\/, \/, \/-}

object CodeProject {

  trait Service {
    def readStructure: \/[Errors.ModuleError, Project]

    def readFile(path: String): \/[Errors.ModuleError, FileContents]
  }

  class CodeProjectService(val name: String, module: CodeModule) extends Service {

    private val projectPrefix = s"projects/${name}/"

    private val defaultVersion = "default"

    override def readStructure: \/[Errors.ModuleError, Project] =
      module getContents s"${projectPrefix}/${name}.json" map readProjectFromString


    override def readFile(path: String): \/[Errors.ModuleError, FileContents] =
      getAlternativeContents(path) flatMap (
        alt => alt.get(defaultVersion)
          .map { defaultContent => \/-(FileContents(defaultContent, alt - defaultVersion)) }
          .getOrElse(-\/(Errors.NoDefaultVersion(name, path)))
        )

    def copyContentTo(destination: Path): Task[CopyChance] =
        module.copy(s"${projectPrefix}/${defaultVersion}", destination)

    private def readFileVersion(path: String, alternative: String) =
      module getContents s"${projectPrefix}/${alternative}/${path}"

    private def getAlternatives =
      module getFolders projectPrefix

    private def getAlternativeContents(path: String): \/[Errors.ModuleError, Map[String, String]] =
      getAlternatives map (_.map(alt => (alt, readFileVersion(path, alt)))
        .filter(onlyExisting)
        .map(emptyIfMissing) toMap)

    private val emptyIfMissing = (tup: (String, \/[_, String])) => (tup._1, tup._2.getOrElse(""))

    private val onlyExisting = (tup: (String, \/[_, String])) => tup._2.isRight
  }

}



