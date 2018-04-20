package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.{Files, Path}

import pl.setblack.strupek.nakolanie.code.Errors._
import pl.setblack.strupek.nakolanie.scanner.CodeProject.CodeProjectService
import scalaz.{-\/, \/, \/-}

import scala.io.Source

object CodeModule {

  type Projects = Seq[Project]
  type ProjectsChance = \/[ModuleError, Projects]

  trait CodeModuleService {
    def getProjects() :ProjectsChance
    def getContents(subPath : String) : \/[ModuleError,String]
    def getProject( name: String) : CodeProjectService
    def getFolders(subPath : String ) : \/[ModuleError, Seq[String]]
  }

  class Project( private val  module : CodeModule, val name : String) {
    def getDescription() : CodeProjectService =
          new CodeProjectService(name, module)
  }

  class CodeModule(private[scanner] val path : Path) extends  CodeModuleService {
    import scala.collection.JavaConverters._

    override def getProjects(): ProjectsChance =
      getFolders("projects").map( _.map( new Project(this, _)))

    override def getProject(name: String): CodeProjectService = new CodeProjectService(name, this)

    override def getContents(subPath : String) : \/[ModuleError,String] = {
      val file = path.resolve(subPath)
      if ( Files.exists(file)) {
        \/-(Source.fromInputStream( Files.newInputStream(file)).mkString)
      } else {
        -\/(MissingProjectFile(file))
      }
    }

    def getFolders( subPath : String) : \/[ModuleError, Seq[String]] = {
      val folder = path.resolve(subPath)
      if (Files.exists(folder)) {
        \/-(Files.newDirectoryStream(folder).asScala.filter(Files.isDirectory(_)).map(_.getFileName.toString).toSeq)
      } else {
        -\/(MissingFolder(folder))
      }
    }
  }

}

