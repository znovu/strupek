package pl.setblack.strupek.nakolanie.scanner

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
    path("code" / Segment) {
      moduleName =>
        get {
        /*  val module = toStatus(modules.codeModule(moduleName)._2)
          val projects = module.flatMap( p => toStatus(p.getProjects))
          val result = projects.map(x => complete(HttpEntity(ContentTypes.`application/json`, write(x.map(_.name)) )))
          result.merge*/

          (for { module <- toStatus(modules.codeModule(moduleName)._2)
              projects <- toStatus(module.getProjects())
         } yield ( complete(HttpEntity(ContentTypes.`application/json`, write(projects.map(_.name)) )))) merge

        }
    }


  private def toStatus( m : Maybe[CodeModule.CodeModule]) =
    m.toRight(complete(HttpResponse(StatusCodes.NotFound, entity = "Not found")))

  private def toStatus[T](v : \/[ModuleError, T] ) =
    v.leftMap( e => complete(HttpResponse(StatusCodes.NotFound, entity = e.desc)))

}
