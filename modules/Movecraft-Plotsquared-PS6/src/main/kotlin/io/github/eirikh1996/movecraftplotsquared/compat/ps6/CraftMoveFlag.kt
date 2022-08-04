package io.github.eirikh1996.movecraftplotsquared.compat.ps6


class CraftMoveFlag constructor(override val allowed : Boolean) : CraftFlag(allowed, "Allows a craft to move inside a plot") {
    override fun flagOf(p0: Boolean): CraftFlag {
        return if (p0)
            CRAFT_FLAG_MOVE_TRUE
        else
            CRAFT_FLAG_MOVE_FALSE
    }

    companion object {
        val CRAFT_FLAG_MOVE_TRUE = CraftMoveFlag(true)
        val CRAFT_FLAG_MOVE_FALSE = CraftMoveFlag(false)
    }
}