package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.Movecraft
import net.countercraft.movecraft.events.CraftDetectEvent
import net.countercraft.movecraft.events.CraftRotateEvent
import net.countercraft.movecraft.events.CraftSinkEvent
import net.countercraft.movecraft.events.CraftTranslateEvent
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.full.isSubclassOf

class MovecraftPlotsquared : JavaPlugin(), Listener {


    lateinit var plotSquaredHandler: PlotSquaredHandler

    override fun onEnable() {
        val packageName = server.javaClass.`package`.name
        val version = packageName.substring(packageName.lastIndexOf(".") + 1)
        val versionNumber = Integer.parseInt(version.split("_")[1])
        val compat : String
        var usePS5 : Boolean
        try {
            Class.forName("com.plotsquared.bukkit.BukkitMain")
            usePS5 = true
        } catch (e : Exception) {
            usePS5 = false;
        }
        if (versionNumber <= 12){
            compat = "legacy";
        } else if (usePS5){
            compat = "ps5"
        } else {
            compat = "v1_13"
        }
        val psHandler = Class.forName("io.github.eirikh1996.movecraftplotsquared.compat." + compat + ".IPlotSquaredHandler").kotlin
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
        UpdateManager.instance.start()
    }

    override fun onLoad() {
        instance = this
    }

    @EventHandler
    fun onCraftTranslate(event : CraftTranslateEvent){
        val oldHitBox : HitBox
        val newHitBox : HitBox
        try {
            val getOldHitBox = CraftRotateEvent::class.java.getDeclaredMethod("getOldHitBox")
            val getNewHitBox = CraftRotateEvent::class.java.getDeclaredMethod("getNewHitBox")
            oldHitBox = getOldHitBox.invoke(event) as HitBox
            newHitBox = getNewHitBox.invoke(event) as HitBox
        } catch (e : Exception) {
            return
        }
        if (plotSquaredHandler.allowedToMove(event.craft, oldHitBox, newHitBox)){
            return
        }
        event.failMessage =
            I18n.getInternationalisedString("Translation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftRotate(event: CraftRotateEvent){
        val oldHitBox : HitBox
        val newHitBox : HitBox
        try {
            val getOldHitBox = CraftRotateEvent::class.java.getDeclaredMethod("getOldHitBox")
            val getNewHitBox = CraftRotateEvent::class.java.getDeclaredMethod("getNewHitBox")
            oldHitBox = getOldHitBox.invoke(event) as HitBox
            newHitBox = getNewHitBox.invoke(event) as HitBox
        } catch (e : Exception) {
            return
        }
        if (plotSquaredHandler.allowedToRotate(event.craft, oldHitBox, newHitBox)){
            return
        }
        event.failMessage =
            I18n.getInternationalisedString("Rotation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftDetect(event : CraftDetectEvent) {
        if (plotSquaredHandler.allowedToPilot(event.craft)) {
            return
        }
        event.failMessage =
            I18n.getInternationalisedString("Rotation - Failed Not allowed to pilot")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftSink(event : CraftSinkEvent) {
        if (plotSquaredHandler.allowedToSink(event.craft)) {
            return
        }
        event.craft.notificationPlayer!!.sendMessage(I18n.getInternationalisedString("Rotation - Failed Not allowed to pilot"))
        event.isCancelled = true
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