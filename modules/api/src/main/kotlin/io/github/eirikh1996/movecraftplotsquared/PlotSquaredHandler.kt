package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.MovecraftLocation
import org.bukkit.World
import org.bukkit.entity.Player


interface PlotSquaredHandler {
    fun allowedToMove(pilot : Player, oldHitBox: Set<MovecraftLocation>, newHitBox: Set<MovecraftLocation>, centerOfCraft : MovecraftLocation, craftWorld: World) : Boolean;
    fun allowedToRotate(pilot : Player, oldHitBox: Set<MovecraftLocation>, newHitBox: Set<MovecraftLocation>, centerOfCraft : MovecraftLocation, craftWorld: World) : Boolean
    fun allowedToPilot(pilot : Player, hitBox: Set<MovecraftLocation>, centerOfCraft : MovecraftLocation, craftWorld: World) : Boolean
    fun allowedToSink(pilot : Player, hitBox: Set<MovecraftLocation>, centerOfCraft : MovecraftLocation, craftWorld: World) : Boolean
    fun insidePlot(hitBox : Set<MovecraftLocation>, craftWorld: World) : Boolean
    fun plotSquaredInstalled() : Boolean
    fun registerPSFlags()
}