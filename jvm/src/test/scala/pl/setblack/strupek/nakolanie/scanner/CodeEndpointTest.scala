package pl.setblack.strupek.nakolanie.scanner

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.ModulesService.FileBasedModulesService
import upickle.default._


class CodeEndpointTest extends FunSpec with Matchers with ScalatestRouteTest{
   describe("endpoint") {
     val modulesService = new FileBasedModulesService(TestResources.modules)
     val endpoint = new CodeEndpoint(modulesService)
     val route = endpoint.createRoute
     it("for test1 module") {
       Get("/code/test1") ~> route ~> check {
         read[Seq[String]](responseAs[String]) should contain allOf   ("bomb", "boolean", "calc", "cardinal")
       }
     }
   }
}
