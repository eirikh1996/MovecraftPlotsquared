package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.events.CraftDetectEvent
import net.countercraft.movecraft.events.CraftRotateEvent
import net.countercraft.movecraft.events.CraftSinkEvent
import net.countercraft.movecraft.events.CraftTranslateEvent
import org.bukkit.event.EventHandler

class Movecraft7Handler(plotSquaredHandler: PlotSquaredHandler) : MovecraftHandler(plotSquaredHandler) {
    @EventHandler
    fun onCraftTranslate(event : CraftTranslateEvent){
        val oldHitBox = event.oldHitBox
        val newHitBox = event.newHitBox
        val craft = event.craft
        if (craft.sinking || plotSquaredHandler.allowedToMove(craft.notificationPlayer!!, oldHitBox.asSet(), newHitBox.asSet(), craft.hitBox.midPoint, craft.w)){
            return
        }
        if (craft.type.cruiseOnPilot && !plotSquaredHandler.insidePlot(craft.hitBox.asSet(), craft.w) && !Settings.AllowCruiseOnPilotCraftsToExitPlots) {
            craft.sink()
            return
        }
        event.failMessage =
            I18n.getInternationalisedString("Translation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftRotate(event: CraftRotateEvent){
        val oldHitBox = event.oldHitBox
        val newHitBox = event.newHitBox
        val craft = event.craft
        if (plotSquaredHandler.allowedToRotate(craft.notificationPlayer!!, oldHitBox.asSet(), newHitBox.asSet(), craft.hitBox.midPoint, craft.w)){
            return
        }
        event.failMessage = I18n.getInternationalisedString("Rotation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftDetect(event : CraftDetectEvent) {
        val craft = event.craft
        if (plotSquaredHandler.allowedToPilot(craft.notificationPlayer!!, craft.hitBox.asSet(),craft.hitBox.midPoint, craft.w)) {
            return
        }
        event.failMessage = I18n.getInternationalisedString("Rotation - Failed Not allowed to pilot")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftSink(event : CraftSinkEvent) {
        val craft = event.craft
        if (plotSquaredHandler.allowedToSink(craft.notificationPlayer!!, craft.hitBox.asSet(), craft.hitBox.midPoint, craft.w)) {
            return
        }
        craft.notificationPlayer!!.sendMessage(I18n.getInternationalisedString("Rotation - Failed Not allowed to pilot"))
        event.isCancelled = true
    }
}