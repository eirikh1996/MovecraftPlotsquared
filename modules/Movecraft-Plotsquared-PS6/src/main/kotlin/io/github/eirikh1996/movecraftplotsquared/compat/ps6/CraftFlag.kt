package io.github.eirikh1996.movecraftplotsquared.compat.ps6

import com.plotsquared.core.configuration.caption.StaticCaption
import com.plotsquared.core.plot.flag.types.BooleanFlag

abstract class CraftFlag constructor(open val allowed : Boolean, caption: String) : BooleanFlag<CraftFlag>(allowed, StaticCaption.of(caption)) {

}