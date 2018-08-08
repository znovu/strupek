package pl.setblack.strupek.nakolanie.compiler.module.hq9

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.scalatest.{AsyncFunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import delorean._
import org.typelevel.scalatest.DisjunctionValues._
import pl.setblack.strupek.nakolanie.compiler.CompilationResult
import pl.setblack.strupek.nakolanie.compiler.CompilationResult.{OutputLine, Started}

class HQ9CompilerTest extends AsyncFunSpec with Matchers {
  implicit val system = ActorSystem("compiler")
  implicit val materializer = ActorMaterializer()
  describe( "hq9 compiler" ) {
    val compiler = new HQ9Compiler()
    val folder = TestResources.hq9sample
    val codeModule = new CodeModule(folder)
    val prj1 = codeModule.getProject("prj1").value

    //val pr1struct = prj1.flatMap { _.readStructure}
    it ( "should start  hq9+ compilation") {
      val compilation = compiler.compile(prj1)
      val result = compilation.runWith( Sink.seq[CompilationResult])

      result map( _.seq.head should  be (Started) )

    }

    it ( "should printe hello world") {
      val compilation = compiler.compile(prj1)
      println("ooo")
      val result = compilation.runWith( Sink.seq[CompilationResult])
      println("ooo1")
      result  map( _.seq.filter{_.isInstanceOf[OutputLine]}.head should  be (OutputLine("Hello, world!")) )
    }
  }

}
