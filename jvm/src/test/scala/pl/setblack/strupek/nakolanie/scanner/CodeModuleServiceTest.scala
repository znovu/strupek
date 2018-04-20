package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.Paths

import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule


class CodeModuleServiceTest extends FunSpec with Matchers {

  import org.typelevel.scalatest.DisjunctionValues._

  describe("codemodule") {
    val testFolder = Paths.get(classOf[CodeModuleServiceTest].getResource("/test1").toURI)
    val service = new CodeModule(testFolder)
    it("should read any projects") {
      service.getProjects().isRight should be(true)
    }

    it("should read 4 projects") {
      service.getProjects().value.size should be(4)
    }

    it("should read calc project") {
      service.getProjects().value.map( _.name) should contain("calc")
    }
  }


}
