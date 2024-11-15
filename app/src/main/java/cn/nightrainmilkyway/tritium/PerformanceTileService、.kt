package cn.nightrainmilkyway.tritium

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.content.Intent
import android.annotation.SuppressLint
import android.util.Log
import java.io.File
import java.io.IOException

@SuppressLint("SdCardPath")
class PerformanceTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        Log.d("PerformanceTileService", "Tile clicked")
        setMode("performance")
        updateTile()
        sendBroadcast(Intent("cn.nightrainmilkyway.tritium.UPDATE_TILE"))
    }

    private fun updateTile() {
        val tile = qsTile
        val currentMode = readModeFromFile()
        tile.state = if (currentMode == "performance") Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = "性能模式"
        tile.updateTile()
        Log.d("PerformanceTileService", "Tile updated: $currentMode")
    }

    private fun setMode(mode: String) {
        writeModeToFile(mode)
    }

    private fun writeModeToFile(mode: String) {
        try {
            val filePath = "/data/data/cn.nightrainmilkyway.tritium/files/binaries/mode.txt"
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "echo $mode > $filePath"))
            process.waitFor()
            Log.d("PerformanceTileService", "Mode written to file: $mode")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PerformanceTileService", "IOException: ${e.message}")
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e("PerformanceTileService", "InterruptedException: ${e.message}")
        }
    }

    private fun readModeFromFile(): String {
        return try {
            val filePath = "/data/data/cn.nightrainmilkyway.tritium/files/binaries/mode.txt"
            val file = File(filePath)
            if (file.exists()) {
                val mode = file.readText().trim()
                Log.d("PerformanceTileService", "Mode read from file: $mode")
                mode
            } else {
                "balance"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PerformanceTileService", "IOException: ${e.message}")
            "balance"
        }
    }
}