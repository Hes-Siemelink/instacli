package instacli.cli

import com.fasterxml.jackson.module.kotlin.readValue
import instacli.core.Yaml.mapper
import java.io.File

class InstacliInfo {

    val imports = mutableListOf<String>()

    companion object {
        fun load(dir: File): InstacliInfo {
            val infoFile = File(dir, "instacli-info.yaml")
            if (infoFile.exists()) {
                return mapper.readValue(infoFile)
            } else {
                return InstacliInfo()
            }
        }
    }
}