package io.github.eirikh1996.movecraftplotsquared.compat.ps6

class CraftPilotFlag constructor(override val allowed : Boolean) : CraftFlag(allowed, "Allows a craft to be piloted inside a plot") {


    override fun flagOf(p0: Boolean): CraftFlag {
        return if (p0)
            CRAFT_FLAG_PILOT_TRUE
        else
            CRAFT_FLAG_PILOT_FALSE
    }
    companion object {
        val CRAFT_FLAG_PILOT_TRUE = CraftPilotFlag(true)
        val CRAFT_FLAG_PILOT_FALSE = CraftPilotFlag(false)
    }
}