package io.github.eirikh1996.movecraftplotsquared

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.scheduler.BukkitRunnable
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class UpdateManager : BukkitRunnable() {
    private var running = false
    companion object{
        private val UPDATE_PERM = "mps.update"
        val instance = UpdateManagerHolder.INSTANCE
    }

    override fun run() {
        val newVersion = checkUpdate(getCurrentVersion())
        val currentVersion = getCurrentVersion()
        MovecraftPlotsquared.instance.logger.info(I18n.getInternationalisedString("Update - Checking for updates"))
        Updater(currentVersion, newVersion).runTaskLater(MovecraftPlotsquared.instance, 1800)
    }

    fun getCurrentVersion() : Double {
        return MovecraftPlotsquared.instance.description.version.toDouble()
    }

    fun checkUpdate(currentVersion : Double) : Double {
        try {
            val url = URL("https://servermods.forgesvc.net/servermods/files?projectids=341060")
            val conn = url.openConnection()
            conn.readTimeout = 5000
            conn.addRequestProperty("User-Agent", "Movecraft-PlotSquared Update Checker")
            conn.doOutput = true
            val reader = BufferedReader(InputStreamReader(conn.getInputStream()))
            val response = reader.readLine()
            val jsonArray = JSONValue.parse(response) as JSONArray
            if (jsonArray.size == 0) {
                MovecraftPlotsquared.instance.logger.warning("No files found, or Feed URL is bad.")
                return currentVersion
            }
            val jsonObject = jsonArray.get(jsonArray.size - 1) as JSONObject
            val versionName = jsonObject.get("name") as String
            val newVersion = versionName.substring(versionName.lastIndexOf("v") + 1)
            return java.lang.Double.parseDouble(newVersion)
        } catch (e: Exception) {
            e.printStackTrace()
            return currentVersion
        }
    }

    fun start(){
        if (running){
            return
        }
        running = true
        runTaskTimerAsynchronously(MovecraftPlotsquared.instance, 0, 1000000)
    }
    fun notifyPlayer(player : Player){
        if (!player.hasPermission(UPDATE_PERM)){
            return
        }
        player.sendMessage(Messages.MPS_PREFIX + I18n.getInternationalisedString("Update - New version"))
        player.sendMessage(Messages.MPS_PREFIX + I18n.getInternationalisedString("Update - Download at") + " https://dev.bukkit.org/projects/movecraft-plotsquared/files")
    }
    class Updater constructor(val currentVersion: Double, val newVersion : Double) : BukkitRunnable(){
        override fun run() {
            if (newVersion <= currentVersion){
                MovecraftPlotsquared.instance.logger.info(I18n.getInternationalisedString("Update - No new version"))
            } else {
                MovecraftPlotsquared.instance.logger.warning(I18n.getInternationalisedString("Update - New version"))
                MovecraftPlotsquared.instance.logger.warning(I18n.getInternationalisedString("Update - Download at") + "https://dev.bukkit.org/projects/movecraft-plotsquared/files")
                Bukkit.broadcast(Messages.MPS_PREFIX + I18n.getInternationalisedString("Update - New version"), UPDATE_PERM)
                Bukkit.broadcast(Messages.MPS_PREFIX + I18n.getInternationalisedString("Update - Download at") + "https://dev.bukkit.org/projects/movecraft-plotsquared/files", UPDATE_PERM)
            }
        }

    }

    private class UpdateManagerHolder{
        companion object {
             val INSTANCE = UpdateManager()
        }
    }
}