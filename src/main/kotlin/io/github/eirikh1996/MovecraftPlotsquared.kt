package io.github.eirikh1996

import com.github.intellectualsites.plotsquared.api.PlotAPI
import com.github.intellectualsites.plotsquared.plot.IPlotMain
import com.github.intellectualsites.plotsquared.plot.`object`.Location
import com.github.intellectualsites.plotsquared.plot.`object`.Plot
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag
import net.countercraft.movecraft.Movecraft
import net.countercraft.movecraft.MovecraftLocation
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.events.CraftRotateEvent
import net.countercraft.movecraft.events.CraftTranslateEvent
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

class MovecraftPlotsquared : JavaPlugin(), Listener {



    override fun onEnable() {
        saveDefaultConfig()
        Settings.Locale = config.getString("Locale")!!
        val craftMoveFlag = BooleanFlag("craftMovement")
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
        val tempPSplugin : Plugin = server.pluginManager.getPlugin("PlotSquared")!!
        if (tempPSplugin is IPlotMain && tempPSplugin.isEnabled){
            plotSquaredPlugin = tempPSplugin
        }
        if (movecraftPlugin == null){
            logger.severe(I18n.getInternationalisedString("Startup - Movecraft Not Found or disabled"))
            server.pluginManager.disablePlugin(this)
        }
        if (plotSquaredPlugin == null){
            logger.severe(I18n.getInternationalisedString("Startup - PlotSquared Not Found or disabled"))
            server.pluginManager.disablePlugin(this)
        }
        plotApi = PlotAPI()
        plotApi.addFlag(craftMoveFlag)

        Settings.AllowMovementOutsidePlots = config.getBoolean("AllowMovementOutsidePlots", false)
        Settings.AllowCruiseOnPilotCraftsToExitPlots = config.getBoolean("AllowCruiseOnPilotCraftsToExitPlots", false)
        server.pluginManager.registerEvents(this, this)

    }

    override fun onLoad() {
        instance = this
    }

    @EventHandler
    fun onCraftTranslate(event : CraftTranslateEvent){
        if (allowedToMove(event.craft, event.oldHitBox, event.newHitBox)){
            return
        }
        event.failMessage = I18n.getInternationalisedString("Translation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftRotate(event: CraftRotateEvent){
        if (allowedToMove(event.craft, event.oldHitBox, event.newHitBox)){
            return
        }
        event.failMessage = I18n.getInternationalisedString("Rotation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent){
        if (event.plugin !is IPlotMain && event.plugin !is Movecraft){
            return
        }
        server.pluginManager.disablePlugin(this)
    }

    @EventHandler
    fun onPluginEnable(event: PluginEnableEvent){
        if (event.plugin !is IPlotMain && event.plugin !is Movecraft){
            return
        }
        server.pluginManager.enablePlugin(this)
    }

    private fun allowedToMove(craft : Craft,oldHitBox: HitBox, newHitBox: HitBox) : Boolean{
        if (craft.sinking){
            return true
        }
        var plot : Plot? = Utils.movecraft2PSLocation(craft.w, craft.hitBox.midPoint).plot
        for (ml : MovecraftLocation in newHitBox){
            val pLoc = Utils.movecraft2PSLocation(craft.w, ml)
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }
        val psWorldsFile = server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
        val input = FileInputStream(psWorldsFile)
        val yaml = Yaml()
        val data : Map<String, Any> = yaml.load<Map<String, Any>>(input)
        val worlds : Map<String, Any> = data.get("worlds") as Map<String, Any>

        if (!worlds.containsKey(craft.w.name)){
            return true
        }
        if (craft.notificationPlayer != null && craft.notificationPlayer!!.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null){
            if (craft.type.cruiseOnPilot && !Settings.AllowCruiseOnPilotCraftsToExitPlots){
                craft.sink()
            }
            return false
        }
        if (!plot.owners.contains(craft.notificationPlayer!!.uniqueId) && !plot.members.contains(craft.notificationPlayer!!.uniqueId)){
            return false
        }
        return true
    }

    companion object {
        lateinit var plotApi : PlotAPI
        private var movecraftPlugin : Movecraft? = null
        private var plotSquaredPlugin : IPlotMain? = null
        @JvmStatic lateinit var instance : MovecraftPlotsquared
    }
}