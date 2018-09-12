package pl.setblack.strupek.nakolanie.server

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem
import pl.setblack.strupek.nakolanie.compiler.session.SessionsEndpoint
import pl.setblack.strupek.nakolanie.context.JVMContext
import pl.setblack.strupek.nakolanie.scanner.{CodeEndpoint, ModuleBasedProjectProvider}
import pl.setblack.strupek.nakolanie.scanner.ModulesService.FileBasedModulesService

import scala.io.StdIn

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    val modules = new FileBasedModulesService(Paths.get("codes"))
    val code = new CodeEndpoint(modules)
    implicit val projectProvider = new ModuleBasedProjectProvider(modules)
    implicit val ctx = JVMContext
    val compilationSystem = new CompilationSessionSystem()
    val sessionsEndpoint = new SessionsEndpoint(compilationSystem)


    val route: Route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~ code.createRoute ~ sessionsEndpoint.createRoute()

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8001)

    println(s"Server online at http://localhost:8001/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => {
      println("exiting")
      system.terminate()
      println("exited")
      System.exit(0)

    }) // and shutdown when

  }
}
