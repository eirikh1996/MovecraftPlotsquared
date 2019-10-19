package io.github.eirikh1996.compat.v1_13

import com.github.intellectualsites.plotsquared.api.PlotAPI
import com.github.intellectualsites.plotsquared.plot.IPlotMain
import com.github.intellectualsites.plotsquared.plot.`object`.Plot
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag
import io.github.eirikh1996.PlotSquaredHandler
import io.github.eirikh1996.Settings
import io.github.eirikh1996.Utils
import net.countercraft.movecraft.MovecraftLocation
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

class IPlotSquaredHandler constructor(val plugin: Plugin): PlotSquaredHandler {
    val plotApi = PlotAPI()
    override fun registerPSFlags() {
        plotApi.addFlag(BooleanFlag("craftMovement"))
    }

    lateinit var plotSquaredPlugin : IPlotMain
    override fun plotSquaredInstalled(): Boolean {
        val ps = plugin.server.pluginManager.getPlugin("PlotSquared")
        val isInstalled = ps is IPlotMain
        if (isInstalled){
            plotSquaredPlugin = ps as IPlotMain
        }
        return isInstalled
    }

    override fun allowedToMove(craft: Craft, oldHitBox: HitBox, newHitBox: HitBox): Boolean {
        if (craft.sinking){
            return true
        }
        var plot : Plot? = Utils.movecraft2PSLocation(craft.w, craft.hitBox.midPoint).plot
        for (ml : MovecraftLocation in newHitBox){
            if (oldHitBox.contains(ml)){
                continue
            }
            val pLoc = Utils.movecraft2PSLocation(craft.w, ml)
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }
        val psWorldsFile = plugin.server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
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
}