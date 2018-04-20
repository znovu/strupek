package pl.setblack.strupek.nakolanie.scanner

import pl.setblack.strupek.nakolanie.code.Code._
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import scalaz.{-\/, \/, \/-}

object CodeProject {

  class CodeProjectService(val name: String, module: CodeModule) {

    private val projectPrefix = s"projects/${name}/"

    private val defaultVersion = "default"

    def readStructure: \/[Errors.ModuleError, Project] =
      module getContents s"${projectPrefix}/${name}.json" map readProjectFromString


    def readFile(path: String): \/[Errors.ModuleError, FileContents] =
      getAlternativeContents(path) flatMap (
        alt => alt.get(defaultVersion)
                    .map{ defaultContent => \/-(FileContents(defaultContent, alt - defaultVersion))}
                    .getOrElse(-\/(Errors.NoDefaultVersion(name, path)))
    )

    private def readFileVersion(path: String, alternative: String) =
      module getContents s"${projectPrefix}/${alternative}/${path}"

    private def getAlternatives =
      module getFolders projectPrefix

    private def getAlternativeContents(path: String): \/[Errors.ModuleError, Map[String, String]] =
      getAlternatives map (_.map ( alt => (alt, readFileVersion(path, alt)) )
        .filter(onlyExisting)
        .map(emptyIfMissing) toMap)

    private val emptyIfMissing = (tup: (String, \/[_, String])) => (tup._1, tup._2.getOrElse(""))

    private val onlyExisting = (tup: (String, \/[_, String])) => tup._2.isRight

  }


}
