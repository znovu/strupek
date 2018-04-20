package pl.setblack.strupek.nakolanie.code

import java.nio.file.Path

object Errors {
  sealed trait ModuleError {
    def desc : String
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

}
