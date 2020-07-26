package io.github.eirikh1996.movecraftplotsquared.compat.v1_13

import com.github.intellectualsites.plotsquared.api.PlotAPI
import com.github.intellectualsites.plotsquared.plot.IPlotMain
import com.github.intellectualsites.plotsquared.plot.`object`.Location
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag
import com.github.intellectualsites.plotsquared.plot.flag.Flags
import io.github.eirikh1996.movecraftplotsquared.PlotSquaredHandler
import io.github.eirikh1996.movecraftplotsquared.Settings
import net.countercraft.movecraft.MovecraftLocation
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.World
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

class IPlotSquaredHandler constructor(val plugin: Plugin):
    PlotSquaredHandler {


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
        val isInstalled = ps is IPlotMain && ps.isEnabled
        if (isInstalled){
            plotSquaredPlugin = ps as IPlotMain
        }
        return isInstalled
    }

    override fun allowedToMove(craft: Craft, oldHitBox: HitBox, newHitBox: HitBox): Boolean {
        if (craft.sinking){
            return true
        }
        val hitbox : HitBox
        try {
            val getHitBox = Craft::class.java.getDeclaredMethod("getHitBox")
            hitbox = getHitBox.invoke(craft) as HitBox
        } catch (e : Exception) {
            return true
        }
        var plot = movecraft2PSLocation(
            craft.w,
            hitbox.midPoint
        ).plot
        for (ml in newHitBox){
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

    override fun allowedToRotate(craft: Craft, oldHitBox: HitBox, newHitBox: HitBox): Boolean {
        if (craft.sinking){
            return true
        }
        val hitbox : HitBox
        try {
            val getHitBox = Craft::class.java.getDeclaredMethod("getHitBox")
            hitbox = getHitBox.invoke(craft) as HitBox
        } catch (e : Exception) {
            return true
        }
        var plot = movecraft2PSLocation(
            craft.w,
            hitbox.midPoint
        ).plot
        for (ml in newHitBox){
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

    override fun allowedToPilot(craft: Craft): Boolean {
        if (craft.sinking){
            return true
        }
        val hitbox : HitBox
        try {
            val getHitBox = Craft::class.java.getDeclaredMethod("getHitBox")
            hitbox = getHitBox.invoke(craft) as HitBox
        } catch (e : Exception) {
            return true
        }
        val plot = movecraft2PSLocation(
            craft.w,
            hitbox.midPoint
        ).plot

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
            if (craft.type.cruiseOnPilot && !Settings.AllowCruiseOnPilotCraftsToExitPlots){
                craft.sink()
            }
            if (!Settings.AllowMovementOutsidePlots){
                return false
            }
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

    override fun allowedToSink(craft: Craft): Boolean {
        val hitbox : HitBox
        try {
            val getHitBox = Craft::class.java.getDeclaredMethod("getHitBox")
            hitbox = getHitBox.invoke(craft) as HitBox
        } catch (e : Exception) {
            return true
        }
        val plot = movecraft2PSLocation(
            craft.w,
            hitbox.midPoint
        ).plot

        if (!worlds.containsKey(craft.w.name)){
            return true
        }
        val notifyP = craft.notificationPlayer
        if (notifyP == null) {
            return true
        }
        if (notifyP.hasPermission("mps.sink.bypassrestrictions")){
            return true
        }
        if (plot == null){
            if (!Settings.AllowMovementOutsidePlots){
                return false
            }
            return true
        }
        if (plot.hasFlag(craftSinkFlag)){
            return plot.getFlag(craftSinkFlag, false)
        }
        if (!Settings.DenySinkOnNoPvP) {
            return true
        }
        return plot.getFlag(Flags.PVP, true)
    }

    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}