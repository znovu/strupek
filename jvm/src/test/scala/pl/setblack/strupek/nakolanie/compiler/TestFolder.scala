package pl.setblack.strupek.nakolanie.compiler

import java.nio.file.{Files, Path, Paths}
class TestFolder(val path  : Path) {

}

object TestFolder {
  val testfilesPath = Paths.get("target/testfolders")
  def createTempFolder( prefix : String) : TestFolder = {
      val tempFilesDir = Files.createDirectories(testfilesPath)
      val createdDir = Files.createTempDirectory(tempFilesDir, prefix)
      createdDir.toFile.deleteOnExit() //!
      new TestFolder(createdDir)
  }
}
