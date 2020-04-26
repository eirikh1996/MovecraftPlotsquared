package io.github.eirikh1996.movecraftplotsquared.compat.ps5

import com.plotsquared.core.configuration.StaticCaption
import com.plotsquared.core.plot.flag.types.BooleanFlag

abstract class CraftFlag constructor(open val allowed : Boolean, caption: StaticCaption) : BooleanFlag<CraftFlag>(allowed, caption) {

}