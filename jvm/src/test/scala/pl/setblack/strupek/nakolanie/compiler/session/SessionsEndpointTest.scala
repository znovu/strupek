package pl.setblack.strupek.nakolanie.compiler.session

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem
import pl.setblack.strupek.nakolanie.scanner.ModuleBasedProjectProvider
import pl.setblack.strupek.nakolanie.scanner.ModulesService.FileBasedModulesService
import pl.setblack.strupek.nakolanie.session.{SessionId, WorkerId}
import pl.setblack.strupek.nakolanie.stratchpad.API.Response
import pl.setblack.strupek.nakolanie.{JVMTestContext, TestResources}
import scalaz.\/-
import upickle.default._

class SessionsEndpointTest extends FunSpec with Matchers with ScalatestRouteTest  {

  val modules = new FileBasedModulesService(TestResources.modules)
  implicit val projectProvider = new ModuleBasedProjectProvider(modules)
  implicit val ctx = JVMTestContext
  val compilationSystem = new CompilationSessionSystem()
  val sessionsEndpoint = new SessionsEndpoint(compilationSystem)
  val route = sessionsEndpoint.createRoute()
  describe("session endpoint") {

     it ("creates session") {
       Post("/session") ~> route ~> check {
          read[SessionId](responseAs[String]).key.length  should be > 5
       }
     }

    it ("reads a project") {
      Post("/session") ~> route ~> check {
        val sessionId = read[SessionId](responseAs[String]).key
        Post( s"/session/${sessionId}/module/hq9sample/project/prj1") ~> route ~> check {
           val worker = responseAs[String]
           worker should be (write(Response.ok(WorkerId("00000000-0000-0000-0000-000000000001"))))
        }
      }
    }
  }

}
