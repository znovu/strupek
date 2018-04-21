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
        pathPrefix(Segment) {
          projectName =>
            path(Segment) {
              fileName =>
                getProject(moduleName , projectName, URLDecoder.decode(fileName, "UTF-8"))
            } ~ getProject(moduleName, projectName)
        } ~ getModule(moduleName)
    }

  private def getProject(moduleName: String, projectName: String, fileName : String) = {
     get {
       (for {module <- toStatus(modules.codeModule(moduleName)._2)
             project = module.getProject(projectName)
             contents <-toStatus(project.readFile(fileName))
       } yield (complete(HttpEntity(ContentTypes.`application/json`, writeJson(contents))))) merge
     }
  }

  private def getProject(moduleName: String, projectName: String) = {
    get {
      (for {module <- toStatus(modules.codeModule(moduleName)._2)
            project = module.getProject(projectName)
            struct <- toStatus(project.readStructure)
      } yield (complete(HttpEntity(ContentTypes.`application/json`, writeJson(struct))))) merge
    }
  }

  private def getModule(moduleName: String) = {
    pathEndOrSingleSlash {
      get {
        (for {module <- toStatus(modules.codeModule(moduleName)._2)
              projects <- toStatus(module.getProjects())
        } yield (complete(HttpEntity(ContentTypes.`application/json`, write(projects.map(_.name)))))) merge

      }
    }
  }

  private def toStatus(m: Maybe[CodeModule.CodeModule]) =
    m.toRight(complete(HttpResponse(StatusCodes.NotFound, entity = "Not found")))

  private def toStatus[T](v: \/[ModuleError, T]) =
    v.leftMap(e => complete(HttpResponse(StatusCodes.NotFound, entity = e.desc)))

}
