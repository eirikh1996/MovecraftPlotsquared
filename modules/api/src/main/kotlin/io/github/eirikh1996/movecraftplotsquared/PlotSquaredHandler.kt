package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.plugin.Plugin

interface PlotSquaredHandler {
    fun allowedToMove(craft : Craft, oldHitBox: HitBox, newHitBox: HitBox) : Boolean;
    fun allowedToRotate(craft : Craft, oldHitBox: HitBox, newHitBox: HitBox) : Boolean
    fun allowedToPilot(craft: Craft) : Boolean
    fun allowedToSink(craft: Craft) : Boolean
    fun plotSquaredInstalled() : Boolean
    fun registerPSFlags()
}