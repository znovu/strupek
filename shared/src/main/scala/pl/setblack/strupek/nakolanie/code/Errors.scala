package pl.setblack.strupek.nakolanie.code

import java.nio.file.Path

object Errors {
  sealed trait ModuleError {
    def desc : String
  }

  final case class MissingModule( val name :String) extends ModuleError {
    override def desc: String = s"Module ${name} is missing"
  }

  final case class MissingFolder( val path :Path) extends ModuleError {
    override def desc: String = s"Path ${path} is missing"
  }

  final case class MissingProjectFile( val path :Path) extends ModuleError {
    override def desc: String = s"Project file ${path} is missing"
  }

  final case class NoDefaultVersion( val prj : String, file : String) extends  ModuleError {
    override def desc: String = s"Default file content ${file} of project ${prj}is missing"
  }

  final case class UnknownCompilationType( val compilation : String ) extends  ModuleError {
    override def desc: String = s"Unknown compilation type ${compilation}"
  }

  sealed trait SessionError {
    def desc : String
  }

  final case class MissingSession(val sessionId: String) extends SessionError {
    override def desc: String = s"Session ${sessionId} not found"
  }

  final case class ErrorInModule(val moduleError: ModuleError) extends SessionError {
    override def desc: String = moduleError.desc
  }

}
