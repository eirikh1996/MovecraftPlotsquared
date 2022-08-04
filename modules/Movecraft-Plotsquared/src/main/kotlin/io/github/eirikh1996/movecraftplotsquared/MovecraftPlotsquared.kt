package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.Movecraft
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

class MovecraftPlotsquared : JavaPlugin(), Listener {


    private lateinit var plotSquaredHandler: PlotSquaredHandler

    override fun onEnable() {
        val packageName = server.javaClass.`package`.name
        val version = packageName.substring(packageName.lastIndexOf(".") + 1)
        val versionNumber = Integer.parseInt(version.split("_")[1])
        val compat : String
        var usePS5 : Boolean
        var usePS6 : Boolean
        try {
            Class.forName("com.plotsquared.bukkit.BukkitMain")
            usePS5 = true
        } catch (e : Exception) {
            usePS5 = false;
        }
        try {
            Class.forName("com.plotsquared.bukkit.BukkitPlatform")
            usePS6 = true
        } catch (e : Exception) {
            usePS6 = false
        }
        if (versionNumber <= 12){
            compat = "legacy";
        } else if (usePS5){
            compat = "ps5"
        } else if (usePS6) {
            compat = "ps6"
        } else {
            compat = "v1_13"
        }
        val psHandler = Class.forName("io.github.eirikh1996.movecraftplotsquared.compat." + compat + ".IPlotSquaredHandler").kotlin
        if (psHandler.isSubclassOf(PlotSquaredHandler::class)){
            plotSquaredHandler = psHandler.primaryConstructor?.call(this) as PlotSquaredHandler
        }

        saveDefaultConfig()
        Settings.Locale = config.getString("Locale")!!

        val locales = arrayOf("en", "de", "nl", "no")
        for (locale in locales){
            val locFile = "mpslang_" + locale + ".properties"
            if (!File(dataFolder.absolutePath + "/localisation/" + locFile).exists()){
                saveResource("localisation/" + locFile, false)
            }
        }
        I18n.initialize(this)
        val tempMovecraftPlugin : Plugin = server.pluginManager.getPlugin("Movecraft")!!
        if (tempMovecraftPlugin is Movecraft && tempMovecraftPlugin.isEnabled){
            movecraftPlugin = tempMovecraftPlugin
        }
        if (movecraftPlugin == null){
            logger.severe(I18n.getInternationalisedString("Startup - Movecraft Not Found or disabled"))
            server.pluginManager.disablePlugin(this)
            return
        }
        if (!plotSquaredHandler.plotSquaredInstalled()){
            logger.severe(I18n.getInternationalisedString("Startup - PlotSquared Not Found or disabled"))
            server.pluginManager.disablePlugin(this)
            return
        }


        var movecraftHandler : MovecraftHandler
        try {
            Class.forName("net.countercraft.movecraft.craft.BaseCraft")
            movecraftHandler = Movecraft8Handler(plotSquaredHandler)
        } catch (e : ClassNotFoundException) {
            movecraftHandler = Movecraft7Handler(plotSquaredHandler)
        }
        logger.info(I18n.getInternationalisedString("Startup - Detected Movecraft Version").replace("%MOVECRAFT_VERSION%", movecraftPlugin!!.description.version))
        Settings.AllowMovementOutsidePlots = config.getBoolean("AllowMovementOutsidePlots", false)
        Settings.AllowCruiseOnPilotCraftsToExitPlots = config.getBoolean("AllowCruiseOnPilotCraftsToExitPlots", false)
        Settings.DenySinkOnNoPvP = config.getBoolean("DenySinkOnNoPvP", false)
        server.pluginManager.registerEvents(movecraftHandler, this)
        server.pluginManager.registerEvents(this, this)
        UpdateManager.instance.start()
    }

    override fun onLoad() {
        instance = this
    }



    @EventHandler
    fun onPlayerJoin(event : PlayerJoinEvent){
        UpdateManager.instance.notifyPlayer(event.player)
    }





    companion object {
        private var movecraftPlugin : Movecraft? = null
        @JvmStatic lateinit var instance : MovecraftPlotsquared
    }
}