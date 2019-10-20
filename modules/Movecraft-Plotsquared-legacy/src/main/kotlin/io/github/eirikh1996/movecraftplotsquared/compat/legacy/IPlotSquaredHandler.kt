package io.github.eirikh1996.movecraftplotsquared.compat.legacy

import com.intellectualcrafters.plot.IPlotMain
import com.intellectualcrafters.plot.`object`.Location
import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.api.PlotAPI
import com.intellectualcrafters.plot.flag.BooleanFlag
import io.github.eirikh1996.movecraftplotsquared.PlotSquaredHandler
import io.github.eirikh1996.movecraftplotsquared.Settings
import net.countercraft.movecraft.MovecraftLocation
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.World
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

class IPlotSquaredHandler constructor(val plugin: Plugin): PlotSquaredHandler {
    val plotApi = PlotAPI()
    private val craftFlag = BooleanFlag("craftMovement")
    override fun registerPSFlags() {

        plotApi.addFlag(craftFlag)
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
        var plot : Plot? = movecraft2PSLocation(
            craft.w,
            craft.hitBox.midPoint
        ).plot
        for (ml : MovecraftLocation in newHitBox){
            if (oldHitBox.contains(ml)){
                continue
            }
            val pLoc =
                movecraft2PSLocation(
                    craft.w,
                    ml
                )
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }
        val psWorldsFile = plugin.server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
        val input = FileInputStream(psWorldsFile)
        val yaml = Yaml()
        val data = yaml.load(input) as Map<String, Any>
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
            if (!Settings.AllowMovementOutsidePlots){
                return false
            }
            return true
        }

        if (!plot.owners.contains(craft.notificationPlayer!!.uniqueId) && !plot.members.contains(craft.notificationPlayer!!.uniqueId)){
            if (plot.hasFlag(craftFlag)){
                return plot.getFlag(craftFlag, false)
            }
            return false
        }
        return true
    }
    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}

