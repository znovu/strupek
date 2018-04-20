package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.Paths

import org.scalatest.{FunSpec, Matchers}
import org.typelevel.scalatest.DisjunctionValues._
import org.scalatest.OptionValues._
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule

class CodeProjectServiceTest extends FunSpec with Matchers {

  describe("a code project") {
    val test1 = TestResources.test1
    val codeModule = new CodeModule(test1)

    val project = codeModule.getProject("calc")
    it("should read struct") {
      project.readStructure.value.files(0).path should be("src/main/scala/badlam/Playfield.scala")
    }
    describe("sample file") {
      val playfieldPath = project.readStructure.value.files(0).path
      val file = project.readFile(playfieldPath)

      it("should have default contents ") {
        file.value.default should include("Array[String]")
      }
      it("should have target contents ") {
        file.value.alternatives.get("target").value should include("Y(G)")
      }
    }

  }
}
