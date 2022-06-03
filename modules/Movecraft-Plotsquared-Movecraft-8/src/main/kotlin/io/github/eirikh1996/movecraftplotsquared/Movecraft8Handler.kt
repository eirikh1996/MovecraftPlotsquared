package io.github.eirikh1996.movecraftplotsquared

import net.countercraft.movecraft.craft.CraftManager
import net.countercraft.movecraft.craft.CruiseOnPilotCraft
import net.countercraft.movecraft.craft.CruiseOnPilotSubCraft
import net.countercraft.movecraft.craft.PilotedCraft
import net.countercraft.movecraft.events.CraftDetectEvent
import net.countercraft.movecraft.events.CraftRotateEvent
import net.countercraft.movecraft.events.CraftSinkEvent
import net.countercraft.movecraft.events.CraftTranslateEvent
import net.countercraft.movecraft.libs.net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler

class Movecraft8Handler(plotSquaredHandler: PlotSquaredHandler) : MovecraftHandler(plotSquaredHandler) {
    @EventHandler
    fun onCraftTranslate(event : CraftTranslateEvent){
        val oldHitBox = event.oldHitBox
        val newHitBox = event.newHitBox
        val craft = event.craft
        if (craft !is PilotedCraft)
            return
        if (plotSquaredHandler.allowedToMove(craft.pilot, oldHitBox.asSet(), newHitBox.asSet(), craft.hitBox.midPoint, craft.w)){
            return
        }
        if ((craft is CruiseOnPilotCraft || craft is CruiseOnPilotSubCraft) && !plotSquaredHandler.insidePlot(craft.hitBox.asSet(), craft.w) && !Settings.AllowCruiseOnPilotCraftsToExitPlots) {
            CraftManager.getInstance().sink(craft)
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
        if (craft !is PilotedCraft)
            return
        if (plotSquaredHandler.allowedToRotate(craft.pilot, oldHitBox.asSet(), newHitBox.asSet(), craft.hitBox.midPoint, craft.w)){
            return
        }
        event.failMessage = I18n.getInternationalisedString("Rotation - Failed Not allowed to move")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftDetect(event : CraftDetectEvent) {
        val craft = event.craft
        if (craft !is PilotedCraft)
            return
        if (plotSquaredHandler.allowedToPilot(craft.pilot, craft.hitBox.asSet(),craft.hitBox.midPoint, craft.w)) {
            return
        }
        event.failMessage = I18n.getInternationalisedString("Rotation - Failed Not allowed to pilot")
        event.isCancelled = true
    }

    @EventHandler
    fun onCraftSink(event : CraftSinkEvent) {
        val craft = event.craft
        if (craft !is PilotedCraft)
            return
        if (plotSquaredHandler.allowedToSink(craft.pilot, craft.hitBox.asSet(), craft.hitBox.midPoint, craft.w)) {
            return
        }
        craft.audience.sendMessage(Component.text(I18n.getInternationalisedString("Rotation - Failed Not allowed to pilot")))
        event.isCancelled = true
    }
}