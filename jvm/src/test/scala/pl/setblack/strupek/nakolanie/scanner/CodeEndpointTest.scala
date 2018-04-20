package pl.setblack.strupek.nakolanie.scanner

import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._


class CodeEndpointTest extends FunSpec with Matchers with ScalatestRouteTest{
   describe("endpoint") {
     val test1 = TestResources.test1
     val codeModule = new CodeModule.CodeModule(test1)
     val endpoint = new CodeEndpoint(codeModule)
     val route = endpoint.createRoute
     it("should say hello") {
       Get("/code") ~> route ~> check {
         responseAs[String] shouldEqual "Captain on the bridge!"
       }
     }
   }
}
