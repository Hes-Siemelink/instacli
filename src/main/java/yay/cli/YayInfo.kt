package yay.cli

import com.fasterxml.jackson.module.kotlin.readValue
import yay.core.Yaml.mapper
import java.io.File

class YayInfo {
    
    val imports = mutableListOf<String>()

    companion object {
        fun load(dir: File): YayInfo {
            val yayInfoFile = File(dir, "yay-info.yaml")
            if (yayInfoFile.exists()) {
                return mapper.readValue(yayInfoFile)
            } else {
                return YayInfo()
            }
        }
    }
}