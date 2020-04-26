package io.github.eirikh1996.movecraftplotsquared.compat.ps5

import com.plotsquared.core.IPlotMain
import com.plotsquared.core.api.PlotAPI
import com.plotsquared.core.location.Location
import com.plotsquared.core.plot.flag.GlobalFlagContainer
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
    private val craftMoveFlag = CraftMoveFlag(false)
    private val craftRotateFlag = CraftRotateFlag(false)
    private val craftPilotFlag = CraftPilotFlag(false)
    private val craftSinkFlag = CraftSinkFlag(false)
    private val worlds : Map<String, Any>
    init {
        val psWorldsFile = plugin.server.pluginManager.getPlugin("PlotSquared")!!.dataFolder.absolutePath + "/config/worlds.yml"
        val input = FileInputStream(psWorldsFile)
        val yaml = Yaml()
        val data = yaml.load(input) as Map<String, Any>
        worlds  = data.get("worlds") as Map<String, Any>
    }
    override fun registerPSFlags() {
        GlobalFlagContainer.getInstance().addFlag(craftMoveFlag)
        GlobalFlagContainer.getInstance().addFlag(craftRotateFlag)
        GlobalFlagContainer.getInstance().addFlag(craftPilotFlag)
        GlobalFlagContainer.getInstance().addFlag(craftSinkFlag)
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
            if (plot.flags.contains(craftMoveFlag)){
                return plot.getFlag(craftMoveFlag)
            }
            return false
        }
        return true
    }

    override fun allowedToRotate(craft: Craft, oldHitBox: HitBox, newHitBox: HitBox) : Boolean {
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
        if (craft.notificationPlayer != null && craft.notificationPlayer!!.hasPermission("mps.rotate.bypassrestrictions")){
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
            if (plot.flags.contains(craftRotateFlag)){
                return plot.getFlag(craftRotateFlag)
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
        var plot = movecraft2PSLocation(
            craft.w,
            hitbox.midPoint
        ).plot

        if (!worlds.containsKey(craft.w.name)){
            return true
        }
        if (craft.notificationPlayer != null && craft.notificationPlayer!!.hasPermission("mps.pilot.bypassrestrictions")){
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
            if (plot.flags.contains(craftPilotFlag)){
                return plot.getFlag(craftPilotFlag)
            }
            return false
        }
        return true
    }

    override fun allowedToSink(craft: Craft): Boolean {
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

        if (!worlds.containsKey(craft.w.name)){
            return true
        }
        if (craft.notificationPlayer != null && craft.notificationPlayer!!.hasPermission("mps.sink.bypassrestrictions")){
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
            if (plot.flags.contains(craftSinkFlag)){
                return plot.getFlag(craftSinkFlag)
            }
        return true
    }

    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}