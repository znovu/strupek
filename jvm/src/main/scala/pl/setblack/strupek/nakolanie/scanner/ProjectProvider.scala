package pl.setblack.strupek.nakolanie.scanner


import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.scanner.ModulesService.ModulesService
import scalaz.\/

trait ProjectProvider {
    def readProject(module :String, project: String) : \/[Errors.ModuleError, CodeProject.Service]
}


class ModuleBasedProjectProvider(private val moduleService : ModulesService) extends ProjectProvider {
    override def readProject(moduleName: String, projectName: String): Errors.ModuleError \/ CodeProject.Service = {
        for {
            module <- moduleService.codeModule(moduleName).\/>(Errors.MissingModule(moduleName))
            project <- module.getProject(projectName)
        } yield (project)
    }
}