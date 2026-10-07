package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class IntruderIncident(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val triggerReason: String, // "Motion / Touch", "Pocket Removal", "Charger Unplugged"
    val photoPath: String? = null,
    val failedPinAttempts: Int = 0
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}

class IntruderLogRepository(context: Context) {
    private val prefs = context.getSharedPreferences("intruder_logs", Context.MODE_PRIVATE)

    fun getIncidents(): List<IntruderIncident> {
        val raw = prefs.getString("incidents_json", null) ?: return emptyList()
        val list = mutableListOf<IntruderIncident>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    IntruderIncident(
                        id = obj.optString("id"),
                        timestamp = obj.optLong("timestamp"),
                        triggerReason = obj.optString("triggerReason"),
                        photoPath = if (obj.isNull("photoPath")) null else obj.optString("photoPath"),
                        failedPinAttempts = obj.optInt("failedPinAttempts", 0)
                    )
                )
            }
        } catch (_: Exception) {}
        return list.sortedByDescending { it.timestamp }
    }

    fun addIncident(incident: IntruderIncident) {
        val current = getIncidents().toMutableList()
        current.add(0, incident)
        // Keep up to 50 logs
        val trimmed = current.take(50)
        saveList(trimmed)
    }

    fun clearAll() {
        prefs.edit().remove("incidents_json").apply()
    }

    private fun saveList(list: List<IntruderIncident>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("timestamp", item.timestamp)
            obj.put("triggerReason", item.triggerReason)
            obj.put("photoPath", item.photoPath)
            obj.put("failedPinAttempts", item.failedPinAttempts)
            array.put(obj)
        }
        prefs.edit().putString("incidents_json", array.toString()).apply()
    }
}
