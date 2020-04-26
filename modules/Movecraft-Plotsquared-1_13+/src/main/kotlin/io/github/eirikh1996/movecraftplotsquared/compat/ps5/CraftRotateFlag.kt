package io.github.eirikh1996.movecraftplotsquared.compat.ps5

import com.plotsquared.core.configuration.StaticCaption

class CraftRotateFlag constructor(override val allowed : Boolean) : CraftFlag(allowed, StaticCaption("Allows a craft to rotate inside a plot")) {

    val CRAFT_FLAG_ROTATE_TRUE = CraftRotateFlag(true)
    val CRAFT_FLAG_ROTATE_FALSE = CraftRotateFlag(false)
    override fun flagOf(p0: Boolean): CraftFlag {
        return if (p0)
            CRAFT_FLAG_ROTATE_TRUE
        else
            CRAFT_FLAG_ROTATE_FALSE
    }
}