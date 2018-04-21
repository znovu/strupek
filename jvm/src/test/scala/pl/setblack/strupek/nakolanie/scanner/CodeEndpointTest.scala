package pl.setblack.strupek.nakolanie.scanner

import java.net.URLEncoder

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.ModulesService.FileBasedModulesService
import pl.setblack.strupek.nakolanie.code.Code._
import pl.setblack.strupek.nakolanie.code.Code.JsonOps._
import upickle.default._


class CodeEndpointTest extends FunSpec with Matchers with ScalatestRouteTest{
   describe("endpoint") {
     val modulesService = new FileBasedModulesService(TestResources.modules)
     val endpoint = new CodeEndpoint(modulesService)
     val route = endpoint.createRoute
     val file = "src/main/scala/badlam/Playfield.scala"
     it("for test1 module") {
       Get("/code/test1") ~> route ~> check {
         read[Seq[String]](responseAs[String]) should contain allOf   ("bomb", "boolean", "calc", "cardinal")
       }
     }
     it("for test1/calc project") {
       Get("/code/test1/calc") ~> route ~> check {
         read[Project](responseAs[String]) should be (Project( "scala", Seq(CompilationFile("src/main/scala/badlam/Playfield.scala"))))
       }
     }

     it(s"for test1/calc/${file} file") {
       val encodedFileName = URLEncoder.encode(file, "UTF-8")
       Get(s"/code/test1/calc/${encodedFileName}") ~> route ~> check {
         read[FileContents](responseAs[String]).default should include ("main(args:")
       }
       Get(s"/code/test1/calc/${encodedFileName}") ~> route ~> check {
         read[FileContents](responseAs[String]).alternatives.size should be (1)
       }
     }
   }
}
