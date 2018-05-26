package pl.setblack.strupek.nakolanie.compiler.session

import pl.setblack.strupek.nakolanie.compiler.CompilationWorker
import pl.setblack.strupek.nakolanie.compiler.CompileService.CloseError
import pl.setblack.strupek.nakolanie.scanner.ProjectProvider
import scalaz.concurrent.Task


object CompilationSession {
    trait Interface {

        def id : SessionId

        def prepare( module : String, project : String) : Task[CompilationWorker]

        def close() : Task[CloseError]
    }

    class Implementation(override val id : SessionId)(implicit projectProvider: ProjectProvider) extends  Interface {

        override def prepare(module: String, project: String): Task[CompilationWorker] = ???

        override def close(): Task[CloseError] = ???
    }
}

