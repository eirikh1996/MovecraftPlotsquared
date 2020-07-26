package io.github.eirikh1996.movecraftplotsquared.compat.legacy

import com.intellectualcrafters.plot.IPlotMain
import com.intellectualcrafters.plot.`object`.Location
import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.api.PlotAPI
import com.intellectualcrafters.plot.flag.BooleanFlag
import com.intellectualcrafters.plot.flag.Flags.PVP
import io.github.eirikh1996.movecraftplotsquared.PlotSquaredHandler
import io.github.eirikh1996.movecraftplotsquared.Settings
import net.countercraft.movecraft.MovecraftLocation
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.World
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

class IPlotSquaredHandler constructor(val plugin: Plugin) : PlotSquaredHandler {
    val plotApi = PlotAPI()

    private val craftMoveFlag = BooleanFlag("craft-move")
    private val craftRotateFlag = BooleanFlag("craft-rotate")
    private val craftPilotFlag = BooleanFlag("craft-pilot")
    private val craftSinkFlag = BooleanFlag("craft-sink")
    private val worlds : Map<String, Any>
    init {
        val psWorldsFile = plugin.server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
        val input = FileInputStream(psWorldsFile)
        val yaml = Yaml()
        val data = yaml.load(input) as Map<String, Any>
        worlds  = data.get("worlds") as Map<String, Any>
    }
    override fun registerPSFlags() {

        plotApi.addFlag(craftMoveFlag)
        plotApi.addFlag(craftRotateFlag)
        plotApi.addFlag(craftPilotFlag)
        plotApi.addFlag(craftSinkFlag)
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
        val notifyP = craft.notificationPlayer
        if (notifyP == null) {
            return true
        }
        if (notifyP.hasPermission("mps.move.bypassrestrictions")){
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

        if (!plot.owners.contains(notifyP.uniqueId) && !plot.members.contains(notifyP.uniqueId) && !plot.trusted.contains(notifyP.uniqueId)){
            if (plot.hasFlag(craftMoveFlag)){
                return plot.getFlag(craftMoveFlag, false)
            }
            return false
        }
        return true
    }

    override fun allowedToPilot(craft: Craft): Boolean {
        if (craft.sinking){
            return true
        }
        val plot : Plot? = movecraft2PSLocation(
            craft.w,
            craft.hitBox.midPoint
        ).plot
        val psWorldsFile = plugin.server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
        val input = FileInputStream(psWorldsFile)
        val yaml = Yaml()
        val data = yaml.load(input) as Map<String, Any>
        val worlds : Map<String, Any> = data.get("worlds") as Map<String, Any>

        if (!worlds.containsKey(craft.w.name)){
            return true
        }
        val notifyP = craft.notificationPlayer
        if (notifyP == null) {
            return true
        }
        if (notifyP.hasPermission("mps.pilot.bypassrestrictions")){
            return true
        }
        if (plot == null){
            return true
        }

        if (!plot.owners.contains(notifyP.uniqueId) && !plot.members.contains(notifyP.uniqueId) && !plot.trusted.contains(notifyP.uniqueId)){
            if (plot.hasFlag(craftPilotFlag)){
                return plot.getFlag(craftPilotFlag, false)
            }
            return false
        }
        return true
    }

    override fun allowedToRotate(craft: Craft, oldHitBox: HitBox, newHitBox: HitBox): Boolean {
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
        val notifyP = craft.notificationPlayer
        if (notifyP == null) {
            return true
        }
        if (notifyP.hasPermission("mps.rotate.bypassrestrictions")){
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

        if (!plot.owners.contains(notifyP.uniqueId) && !plot.members.contains(notifyP.uniqueId) && !plot.trusted.contains(notifyP.uniqueId)){
            if (plot.hasFlag(craftRotateFlag)){
                return plot.getFlag(craftRotateFlag, false)
            }
            return false
        }
        return true
    }

    override fun allowedToSink(craft: Craft): Boolean {
        if (craft.sinking){
            return true
        }
        val plot : Plot? = movecraft2PSLocation(
            craft.w,
            craft.hitBox.midPoint
        ).plot
        val psWorldsFile = plugin.server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
        val input = FileInputStream(psWorldsFile)
        val yaml = Yaml()
        val data = yaml.load(input) as Map<String, Any>
        val worlds : Map<String, Any> = data.get("worlds") as Map<String, Any>

        if (!worlds.containsKey(craft.w.name)){
            return true
        }
        if (craft.notificationPlayer != null && craft.notificationPlayer!!.hasPermission("mps.pilot.bypassrestrictions")){
            return true
        }
        if (plot == null){
            return true
        }

        if (plot.hasFlag(craftSinkFlag)){
            return plot.getFlag(craftSinkFlag, false)
        }
        if (!Settings.DenySinkOnNoPvP) {
            return true
        }
        return plot.getFlag(PVP, true)
    }

    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}

