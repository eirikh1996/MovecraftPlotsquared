package io.github.eirikh1996.movecraftplotsquared.compat.ps5

import com.plotsquared.core.configuration.StaticCaption

class CraftMoveFlag constructor(override val allowed : Boolean) : CraftFlag(allowed, StaticCaption("Allows a craft to move inside a plot")) {

    val CRAFT_FLAG_MOVE_TRUE = CraftMoveFlag(true)
    val CRAFT_FLAG_MOVE_FALSE = CraftMoveFlag(false)
    override fun flagOf(p0: Boolean): CraftFlag {
        return if (p0)
            CRAFT_FLAG_MOVE_TRUE
        else
            CRAFT_FLAG_MOVE_FALSE
    }
}