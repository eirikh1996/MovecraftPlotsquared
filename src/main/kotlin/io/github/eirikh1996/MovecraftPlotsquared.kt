package io.github.eirikh1996

import com.github.intellectualsites.plotsquared.api.PlotAPI
import com.github.intellectualsites.plotsquared.plot.IPlotMain
import com.github.intellectualsites.plotsquared.plot.PlotSquared
import com.github.intellectualsites.plotsquared.plot.`object`.Plot
import com.github.intellectualsites.plotsquared.plot.`object`.PlotArea
import com.github.intellectualsites.plotsquared.plot.`object`.PlotPlayer
import net.countercraft.movecraft.Movecraft
import net.countercraft.movecraft.MovecraftLocation
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.events.CraftRotateEvent
import net.countercraft.movecraft.events.CraftTranslateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class MovecraftPlotsquared : JavaPlugin(), Listener {



    override fun onEnable() {

        val tempMovecraftPlugin : Plugin = server.pluginManager.getPlugin("Movecraft")!!
        if (tempMovecraftPlugin is Movecraft){
            movecraftPlugin = tempMovecraftPlugin
        }
        val tempPSplugin : Plugin = server.pluginManager.getPlugin("PlotSquared")!!
        if (tempPSplugin is IPlotMain){
            plotSquaredPlugin = tempPSplugin
        }
        plotApi = PlotAPI()
        server.pluginManager.registerEvents(this, this)

    }

    override fun onLoad() {
        instance = this
    }

    @EventHandler
    fun onCraftTranslate(event : CraftTranslateEvent){
        val craft : Craft = event.craft;
        if (craft.sinking){
            return
        }
        var plot : Plot
        val pilot : PlotPlayer = plotSquaredPlugin!!.wrapPlayer(craft.notificationPlayer)

        for (ml : MovecraftLocation in craft.hitBox){
            val plotArea : PlotArea? = PlotSquared.get().getApplicablePlotArea(MathUtils.movecraft2PSLocation(craft.w, ml))
            if (plotArea == null && !Settings.allowMovementOnPaths){

            }

        }

    }

    @EventHandler
    fun onCraftRotate(event: CraftRotateEvent){

    }

    companion object {
        private lateinit var plotApi : PlotAPI
        private var movecraftPlugin : Movecraft? = null
        private var plotSquaredPlugin : IPlotMain? = null
        @JvmStatic private lateinit var instance : MovecraftPlotsquared
        @JvmStatic fun getInstance() : MovecraftPlotsquared { return instance }
    }

    fun getPlotAPI() : PlotAPI {
        return plotApi
    }
}