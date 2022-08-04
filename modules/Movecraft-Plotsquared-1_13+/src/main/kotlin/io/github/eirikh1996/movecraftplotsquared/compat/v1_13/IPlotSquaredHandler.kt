package io.github.eirikh1996.movecraftplotsquared.compat.v1_13

import com.github.intellectualsites.plotsquared.api.PlotAPI
import com.github.intellectualsites.plotsquared.plot.IPlotMain
import com.github.intellectualsites.plotsquared.plot.`object`.Location
import com.github.intellectualsites.plotsquared.plot.`object`.Plot
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag
import com.github.intellectualsites.plotsquared.plot.flag.Flags
import com.github.intellectualsites.plotsquared.plot.generator.GeneratorWrapper
import io.github.eirikh1996.movecraftplotsquared.PlotSquaredHandler
import io.github.eirikh1996.movecraftplotsquared.Settings
import net.countercraft.movecraft.MovecraftLocation
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
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

        if (craftWorld.generator !is GeneratorWrapper<*>) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.hasFlag(craftMoveFlag)){
                return plot.getFlag(craftMoveFlag, false)
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

        if (craftWorld.generator !is GeneratorWrapper<*>) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.hasFlag(craftRotateFlag)){
                return plot.getFlag(craftRotateFlag, false)
            }
            return false
        }
        return true
    }

    override fun allowedToPilot(
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

        if (craftWorld.generator !is GeneratorWrapper<*>) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.hasFlag(craftPilotFlag)){
                return plot.getFlag(craftPilotFlag, false)
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

        if (craftWorld.generator !is GeneratorWrapper<*>) {
            return true
        }
        if (pilot.hasPermission("mps.move.bypassrestrictions")){
            return true
        }
        if (plot == null && !Settings.AllowMovementOutsidePlots){
            return false
        }
        if (!plot.owners.contains(pilot.uniqueId) && !plot.members.contains(pilot.uniqueId) && !plot.trusted.contains(pilot.uniqueId)){
            if (plot.hasFlag(craftSinkFlag)){
                return plot.getFlag(craftSinkFlag, false)
            }
            return false
        }
        return plot.getFlag(Flags.PVP, true)
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

    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}