package io.github.eirikh1996

import org.bukkit.entity.Player
import java.io.*
import java.util.*

class I18n {

    companion object {
        var languageFile : Properties = Properties()
        @JvmStatic fun getInternationalisedString(key : String) : String {
            val ret = languageFile.getProperty(key)
            return ret ?: key
        }

        @JvmStatic fun initialize(){
            languageFile = Properties()
            val locDir = File(MovecraftPlotsquared.instance.dataFolder, "localisation")
            if (!locDir.exists()) {
                locDir.mkdirs()
            }
            val locFile = File(locDir, String.format("mpslang_%s.properties", Settings.Locale))
            var `is`: InputStream?
            try {
                `is` = FileInputStream(locFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                `is` = null
            }

            if (`is` == null) {

                MovecraftPlotsquared.instance.getServer().shutdown()
            }
            try {
                languageFile.load(`is`)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}