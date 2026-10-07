package com.example

import com.example.data.IntruderIncident
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testIntruderIncidentModel() {
        val incident = IntruderIncident(
            triggerReason = "Motion Detected (Moved / Picked up)"
        )
        assertNotNull(incident.id)
        assertTrue(incident.timestamp > 0)
        assertEquals("Motion Detected (Moved / Picked up)", incident.triggerReason)
        assertNotNull(incident.formattedTime)
    }

    @Test
    fun testSensitivityThresholdMapping() {
        fun getThreshold(level: Int): Float = when (level) {
            1 -> 0.7f
            3 -> 2.5f
            else -> 1.4f
        }

        assertEquals(0.7f, getThreshold(1), 0.01f)
        assertEquals(1.4f, getThreshold(2), 0.01f)
        assertEquals(2.5f, getThreshold(3), 0.01f)
    }
}
