package io.github.eirikh1996

import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import java.util.*

class I18n {

    companion object {
        @JvmStatic fun sendLocalisedMessage(recipient : Player, key : String){
            val locale : String = recipient.locale
            var langFile : File = File(MovecraftPlotsquared.getInstance().dataFolder.absolutePath + "mpslang_" + locale + ".properties");
            if (!langFile.exists()){
                langFile = File(MovecraftPlotsquared.getInstance().dataFolder.absolutePath + "mpslang_" + Settings.defaultLocale + ".properties")
            }
            val properties : Properties = Properties()
            val input : InputStream = FileInputStream(langFile)
            properties.load(input)
            val message : String = properties.getProperty(key, key)
            recipient.sendMessage(message)
        }
    }
}