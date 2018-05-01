package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.{Files, Paths}

import org.scalatest.{AsyncFunSpec, FunSpec, Matchers}
import org.typelevel.scalatest.DisjunctionValues._
import org.scalatest.OptionValues._
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.CodeModule.{CodeModule, PathDoesNotExist}


object CodeProjectTestHelper {
  def getTestProject: CodeProject.CodeProjectService = {
    val test1 = TestResources.test1
    val codeModule = new CodeModule(test1)
    codeModule.getProject("calc")
  }
}

class CodeProjectServiceTest extends FunSpec with Matchers {

  describe("a code project") {
    val project = CodeProjectTestHelper.getTestProject
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




class  CodeProjectServiceAsyncTest extends AsyncFunSpec with Matchers {
  import delorean._
  describe("a code project") {
    val project = CodeProjectTestHelper.getTestProject
    it("should not copy to non existing dest") {
        val nonExistingPath = Paths.get("om143213/hopefullyDoes/notExist/anywhere")
      project.copyContentTo(nonExistingPath).unsafeToFuture map ( copyResult =>
            copyResult.leftValue shouldBe a [PathDoesNotExist]
        )
    }


    it ("should copy default project content") {
      val tempDir = Files.createTempDirectory("strupekTest")
      project.copyContentTo(tempDir).unsafeToFuture map ( copiedPath =>
        Files.exists(copiedPath.value.resolve("src/main/scala/badlam/Playfield.scala")) should be(true)
        )
    }
  }

}