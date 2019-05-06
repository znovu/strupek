package pl.setblack.strupek.nakolanie.compiler.session

import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.code.Errors.ModuleError
import pl.setblack.strupek.nakolanie.compiler.CompilationWorker
import pl.setblack.strupek.nakolanie.compiler.CompileService.CloseError
import pl.setblack.strupek.nakolanie.compiler.inmem.InMemCode
import pl.setblack.strupek.nakolanie.compiler.module.hq9.HQ9Compiler
import pl.setblack.strupek.nakolanie.compiler.session.workers.InMemWorker
import pl.setblack.strupek.nakolanie.context.Context
import pl.setblack.strupek.nakolanie.scanner.ProjectProvider
import pl.setblack.strupek.nakolanie.session.{SessionId, WorkerId}
import scalaz.concurrent.Task
import scalaz.{-\/, \/}


object CompilationSession {

  trait CompilationSessionAPI {

    def id: SessionId

    def prepare(module: String, project: String):  Task[ModuleError \/ CompilationWorker]

    def close(): Task[CloseError]

    def getWorker( id : WorkerId) : Task[ModuleError \/ CompilationWorker]
  }

  class InMemCompilationSession(override val id: SessionId)(implicit projectProvider: ProjectProvider, implicit val ctx : Context) extends CompilationSessionAPI {

    override def prepare(module: String, project: String): Task[ModuleError \/ CompilationWorker] = {
      val projectData = projectProvider.readProject(module, project)
      val compilation: ModuleError \/ CompilationWorker = projectData.flatMap { prjService =>
        val projectStruct = prjService.readStructure
        projectStruct.flatMap { prj =>
            if ( prj.compilationType == "hq9+" ) {
               InMemCode.fromProject(prjService).map ( new InMemWorker(_, new HQ9Compiler(), ctx.uuidProvider()))
            } else {
                -\/(Errors.UnknownCompilationType(prj.compilationType)) //untested
            }
        }

      }
      Task.point(  compilation)
    }

    override def close(): Task[CloseError] = ???

    override def getWorker(id: WorkerId): Task[ModuleError \/ CompilationWorker] = ???
  }

}

