package io.github.eirikh1996.movecraftplotsquared.compat.ps6

class CraftSinkFlag constructor(override val allowed : Boolean) : CraftFlag(allowed, "Allows a craft to be piloted inside a plot") {


    override fun flagOf(p0: Boolean): CraftFlag {
        return if (p0)
            CRAFT_FLAG_SINK_TRUE
        else
            CRAFT_FLAG_SINK_FALSE
    }

    companion object {
        val CRAFT_FLAG_SINK_TRUE = CraftSinkFlag(true)
        val CRAFT_FLAG_SINK_FALSE = CraftSinkFlag(false)
    }
}