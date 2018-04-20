package pl.setblack.strupek.nakolanie

object API {


  case class CompilationId ( project : String, module : String)


  /**
    * Long session (user is authenticated)
    */
  case class UserSessionId (id :String)

  /**
    * single compliation session
    */
  case class CompilationSessionId(id : String)

  class RegistrationService {
      def startUserSession(login: String, token :String ): UserSessionId = ???

  }

  class  UserService {
      def startCompilation(user: UserSessionId, compilationId: CompilationId)  = ???
  }

  class CompilationService {
     def getFiles() : Seq[CompilationFile]  = ???

    def compile() : CompileResult = ???

  }




  case class FileId(id: String)

  case class CompilationFile ( id : FileId, content : String, alternatives: Seq[AlternativeContent])

  case class AlternativeContent( alternativeId : String, content :String)

  sealed trait CompileCommand {
    class Compile extends  CompileCommand {}

    case class SendFile( file : FileId,  content:String) extends CompileCommand

  }

  case class CompileResult(out: String, error : String)

}


