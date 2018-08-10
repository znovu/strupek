package pl.setblack.strupek.nakolanie.scanner


import pl.setblack.strupek.nakolanie.code.Errors
import scalaz.\/

trait ProjectProvider {
    def readProject(module :String, project: String) : \/[Errors.ModuleError, CodeProject.Service]
}


