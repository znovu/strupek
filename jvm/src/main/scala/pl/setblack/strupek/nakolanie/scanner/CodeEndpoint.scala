package pl.setblack.strupek.nakolanie.scanner

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModuleService

class CodeEndpoint(private val codeModule: CodeModuleService) {

  def createRoute: Route =
    path("code") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>code here</h1>"))
      }
    }


}
