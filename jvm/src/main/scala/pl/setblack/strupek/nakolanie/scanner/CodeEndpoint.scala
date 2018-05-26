package pl.setblack.strupek.nakolanie.scanner

import java.net.URLDecoder

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import pl.setblack.strupek.nakolanie.scanner.ModulesService.ModulesService
import pl.setblack.strupek.nakolanie.code.Code._
import pl.setblack.strupek.nakolanie.code.Errors.ModuleError
import scalaz.{Maybe, \/}
import upickle.default._


class CodeEndpoint(private val modules: ModulesService) {

  def createRoute: Route =
    pathPrefix("code" / Segment) {
      moduleName =>
        val module: \/[StandardRoute, CodeModule.CodeModule] = toStatus(modules.codeModule(moduleName)._2)
        pathPrefix(Segment) {
          projectName =>
            path(Segment) {
              fileName =>
                getFileContents(module, projectName, URLDecoder.decode(fileName, "UTF-8"))
            } ~ getProject(module, projectName)
        } ~ getModule(module)
    }

  private def getFileContents(moduleChance: \/[StandardRoute, CodeModule.CodeModule], projectName: String, fileName: String) =
    get {
      (for {module <- moduleChance
            project <- toStatus(module.getProject(projectName))
            contents <- toStatus(project.readFile(fileName))
      } yield toResult(writeJson(contents))) merge
    }

  private def getProject(moduleChance: \/[StandardRoute, CodeModule.CodeModule], projectName: String) =
    get {
      (for {module <- moduleChance
            project <- toStatus(module.getProject(projectName))
            struct <- toStatus(project.readStructure)
      } yield toResult(writeJson(struct))) merge
    }

  private def getModule(moduleChance: \/[StandardRoute, CodeModule.CodeModule]) =
    pathEndOrSingleSlash {
      get {
        (for { module <- moduleChance
              projects <- toStatus(module.getProjects())
        } yield toResult(write(projects.map(_.name)))) merge
      }
    }

  private def toResult(res: String) = complete(HttpEntity(ContentTypes.`application/json`, res))

  private def toStatus(m: Maybe[CodeModule.CodeModule]) =
    m.toRight(complete(HttpResponse(StatusCodes.NotFound, entity = "Not found")))

  private def toStatus[T](v: \/[ModuleError, T]) =
    v.leftMap(e => complete(HttpResponse(StatusCodes.NotFound, entity = e.desc)))
}
