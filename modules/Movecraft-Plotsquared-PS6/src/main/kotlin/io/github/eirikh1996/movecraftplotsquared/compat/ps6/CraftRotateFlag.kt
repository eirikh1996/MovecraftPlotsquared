package io.github.eirikh1996.movecraftplotsquared.compat.ps6

class CraftRotateFlag constructor(override val allowed : Boolean) : CraftFlag(allowed, "Allows a craft to rotate inside a plot") {


    override fun flagOf(p0: Boolean): CraftFlag {
        return if (p0)
            CRAFT_FLAG_ROTATE_TRUE
        else
            CRAFT_FLAG_ROTATE_FALSE
    }

    companion object {
        val CRAFT_FLAG_ROTATE_TRUE = CraftRotateFlag(true)
        val CRAFT_FLAG_ROTATE_FALSE = CraftRotateFlag(false)
    }
}