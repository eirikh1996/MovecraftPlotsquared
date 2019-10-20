package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.plugin.Plugin

interface PlotSquaredHandler {
    fun allowedToMove(craft : Craft, oldHitBox: HitBox, newHitBox: HitBox) : Boolean;
    fun plotSquaredInstalled() : Boolean
    fun registerPSFlags()
}