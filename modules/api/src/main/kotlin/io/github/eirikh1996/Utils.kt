package io.github.eirikh1996

import com.github.intellectualsites.plotsquared.plot.`object`.Location
import net.countercraft.movecraft.MovecraftLocation
import org.bukkit.World

class Utils {
    companion object {
        @JvmStatic fun movecraft2PSLocation(world : World, movecraftLocation: MovecraftLocation) : Location{
            return Location(world.name, movecraftLocation.x, movecraftLocation.y, movecraftLocation.z)
        }
    }
}