package io.github.eirikh1996.movecraftplotsquared.compat.ps5

import com.plotsquared.bukkit.BukkitMain
import com.plotsquared.bukkit.generator.BukkitPlotGenerator
import com.plotsquared.core.api.PlotAPI
import com.plotsquared.core.location.Location
import com.plotsquared.core.plot.Plot
import com.plotsquared.core.plot.flag.GlobalFlagContainer
import com.plotsquared.core.plot.flag.implementations.PvpFlag
import io.github.eirikh1996.movecraftplotsquared.PlotSquaredHandler
import io.github.eirikh1996.movecraftplotsquared.Settings
import net.countercraft.movecraft.MovecraftLocation
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

class IPlotSquaredHandler constructor(private val plugin: Plugin):
    PlotSquaredHandler {


    val plotApi = PlotAPI()
    private val craftMoveFlag = CraftMoveFlag(false)
    private val craftRotateFlag = CraftRotateFlag(false)
    private val craftPilotFlag = CraftPilotFlag(false)
    private val craftSinkFlag = CraftSinkFlag(false)

    override fun registerPSFlags() {
        GlobalFlagContainer.getInstance().addFlag(craftMoveFlag)
        GlobalFlagContainer.getInstance().addFlag(craftRotateFlag)
        GlobalFlagContainer.getInstance().addFlag(craftPilotFlag)
        GlobalFlagContainer.getInstance().addFlag(craftSinkFlag)
    }

    lateinit var plotSquaredPlugin : BukkitMain
    override fun plotSquaredInstalled(): Boolean {
        val ps = plugin.server.pluginManager.getPlugin("PlotSquared")
        val isInstalled = ps is BukkitMain && ps.isEnabled
        if (isInstalled){
            plotSquaredPlugin = ps as BukkitMain
        }
        return isInstalled
    }

    override fun allowedToMove(
        pilot: Player,
        oldHitBox: Set<MovecraftLocation>,
        newHitBox: Set<MovecraftLocation>,
        centerOfCraft: MovecraftLocation,
        craftWorld: World
    ): Boolean {

        var plot = movecraft2PSLocation(
            craftWorld,
            centerOfCraft
        ).plot
        for (ml in newHitBox){
            if (oldHitBox.contains(ml)){
                continue
            }
            val pLoc =
                movecraft2PSLocation(
                    craftWorld,
                    ml
                )
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }

        if (craftWorld.generator !is BukkitPlotGenerator) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.flags.contains(craftMoveFlag)){
                return plot.getFlag(craftMoveFlag)
            }
            return false
        }
        return true
    }

    override fun allowedToRotate(
        pilot: Player,
        oldHitBox: Set<MovecraftLocation>,
        newHitBox: Set<MovecraftLocation>,
        centerOfCraft: MovecraftLocation,
        craftWorld: World
    ): Boolean {
        var plot = movecraft2PSLocation(
            craftWorld,
            centerOfCraft
        ).plot
        for (ml in newHitBox){
            if (oldHitBox.contains(ml)){
                continue
            }
            val pLoc =
                movecraft2PSLocation(
                    craftWorld,
                    ml
                )
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }

        if (craftWorld.generator !is BukkitPlotGenerator) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.flags.contains(craftRotateFlag)){
                return plot.getFlag(craftRotateFlag)
            }
            return false
        }
        return true
    }

    override fun insidePlot(hitBox: Set<MovecraftLocation>, craftWorld: World): Boolean {
        var plot : Plot? = null
        for (ml in hitBox) {
            plot = movecraft2PSLocation(craftWorld, ml).plot
            if (plot != null)
                break
        }

        for (ml in hitBox) {
            plot = movecraft2PSLocation(craftWorld, ml).plot
            if (plot == null)
                break
        }
        return plot != null
    }

    override fun allowedToPilot(pilot: Player, hitBox: Set<MovecraftLocation>, centerOfCraft: MovecraftLocation, craftWorld: World): Boolean {
        var plot = movecraft2PSLocation(
            craftWorld,
            centerOfCraft
        ).plot
        for (ml in hitBox){
            val pLoc =
                movecraft2PSLocation(
                    craftWorld,
                    ml
                )
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }

        if (craftWorld.generator !is BukkitPlotGenerator) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.flags.contains(craftPilotFlag)){
                return plot.getFlag(craftPilotFlag)
            }
            return false
        }
        return true
    }

    override fun allowedToSink(
        pilot: Player,
        hitBox: Set<MovecraftLocation>,
        centerOfCraft: MovecraftLocation,
        craftWorld: World
    ): Boolean {
        var plot = movecraft2PSLocation(
            craftWorld,
            centerOfCraft
        ).plot
        for (ml in hitBox){

            val pLoc =
                movecraft2PSLocation(
                    craftWorld,
                    ml
                )
            if (pLoc.plot != null){
                continue
            }
            plot = null
            break
        }

        if (craftWorld.generator !is BukkitPlotGenerator) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.flags.contains(craftMoveFlag)){
                return plot.getFlag(craftMoveFlag)
            }
            return false
        }
        return plot.getFlag(PvpFlag::class.java)
    }

    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}