package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.{Files, Path}

import org.apache.commons.io.FileUtils
import pl.setblack.strupek.nakolanie.code.Errors._
import pl.setblack.strupek.nakolanie.scanner.CodeProject.CodeProjectService
import scalaz.concurrent.Task
import scalaz.{-\/, \/, \/-}

import scala.io.Source

object CodeModule {

  type Projects = Seq[Project]
  type ProjectsChance = \/[ModuleError, Projects]
  type CopyChance = CopyError \/ Path

  trait CodeModuleService {
    def getProjects(): ProjectsChance

    def getProject(name: String): \/[ModuleError, CodeProjectService]

    //todo split to External and Internal trait  - those below should not be exposed
    def getFolders(subPath: String): \/[ModuleError, Seq[String]]

    def getContents(subPath: String): \/[ModuleError, String]

    def copy(subPath: String, destination: Path): Task[CopyChance]
  }

  class Project(private val module: CodeModule, val name: String) {
    def getDescription(): CodeProjectService =
      new CodeProjectService(name, module)
  }

  class CodeModule(private[scanner] val path: Path) extends CodeModuleService {

    import scala.collection.JavaConverters._

    override def getProjects(): ProjectsChance =
      getFolders("projects").map(_.map(new Project(this, _)))

    override def getProject(name: String): \/[ModuleError, CodeProjectService] = {
      val project = new CodeProjectService(name, this)
      project.readStructure.map( _ => project)
    }

    override def getContents(subPath: String): \/[ModuleError, String] = {
      val file = path.resolve(subPath)
      if (Files.exists(file)) {
        \/-(Source.fromInputStream(Files.newInputStream(file)).mkString)
      } else {
        -\/(MissingProjectFile(file))
      }
    }

    override def getFolders(subPath: String): \/[ModuleError, Seq[String]] = {
      val folder = path.resolve(subPath)
      if (Files.exists(folder)) {
        \/-(Files.newDirectoryStream(folder).asScala.filter(Files.isDirectory(_)).map(_.getFileName.toString).toSeq)
      } else {
        -\/(MissingFolder(folder))
      }
    }

    override def copy(subPath: String, destination: Path): Task[CopyChance] = Task {
      if (Files.exists(destination)) {
        FileUtils.copyDirectory(path.resolve(subPath).toFile, destination.toFile)
        \/-(destination)
      } else {
        -\/(PathDoesNotExist(destination))
      }
    }
  }

  sealed trait CopyError

  final case class PathDoesNotExist(path: Path) extends CopyError

}

