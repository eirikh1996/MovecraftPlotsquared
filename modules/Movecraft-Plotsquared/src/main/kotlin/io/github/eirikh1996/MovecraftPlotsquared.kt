package io.github.eirikh1996

import net.countercraft.movecraft.Movecraft
import net.countercraft.movecraft.events.CraftRotateEvent
import net.countercraft.movecraft.events.CraftTranslateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class MovecraftPlotsquared : JavaPlugin(), Listener {


    lateinit var plotSquaredHandler: PlotSquaredHandler
    override fun onEnable() {
        val packageName = server.javaClass.`package`.name
        val version = packageName.substring(packageName.lastIndexOf(".") + 1)
        val versionNumber = Integer.parseInt(version.split("_")[1])
        val compat : String
        if (versionNumber <= 12){
            compat = "legacy";
        } else {
            compat = "v1_13"
        }
        val psHandler = Class.forName("io.github.eirikh1996.compat." + compat + ".IPlotSquaredHandler").kotlin
        if (psHandler.isSubclassOf(PlotSquaredHandler::class)){
            plotSquaredHandler = psHandler.constructors.first().call(this) as PlotSquaredHandler
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
        I18n.initialize()
        val tempMovecraftPlugin : Plugin = server.pluginManager.getPlugin("Movecraft")!!
        if (tempMovecraftPlugin is Movecraft && tempMovecraftPlugin.isEnabled){
            movecraftPlugin = tempMovecraftPlugin
        }
        if (movecraftPlugin == null){
            logger.severe(I18n.getInternationalisedString("Startup - Movecraft Not Found or disabled"))
            server.pluginManager.disablePlugin(this)
        }
        if (!plotSquaredHandler.plotSquaredInstalled()){
            logger.severe(I18n.getInternationalisedString("Startup - PlotSquared Not Found or disabled"))
            server.pluginManager.disablePlugin(this)
        }

        Settings.AllowMovementOutsidePlots = config.getBoolean("AllowMovementOutsidePlots", false)
        Settings.AllowCruiseOnPilotCraftsToExitPlots = config.getBoolean("AllowCruiseOnPilotCraftsToExitPlots", false)
        server.pluginManager.registerEvents(this, this)

    }

    override fun onLoad() {
        instance = this
    }

    @EventHandler
    fun onCraftTranslate(event : CraftTranslateEvent){
        if (plotSquaredHandler.allowedToMove(event.craft, event.oldHitBox, event.newHitBox)){
            return
        }
        event.failMessage = I18n.getInternationalisedString("Translation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftRotate(event: CraftRotateEvent){
        if (plotSquaredHandler.allowedToMove(event.craft, event.oldHitBox, event.newHitBox)){
            return
        }
        event.failMessage = I18n.getInternationalisedString("Rotation - Failed Not allowed to move")
        event.isCancelled = true
    }





    companion object {
        private var movecraftPlugin : Movecraft? = null
        @JvmStatic lateinit var instance : MovecraftPlotsquared
    }
}