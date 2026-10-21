package com.andrerinas.openheadunit.main

import com.andrerinas.openheadunit.main.HomeStatusPolicy.Link
import com.andrerinas.openheadunit.main.HomeStatusPolicy.Status
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeStatusPolicyTest {

    @Test
    fun `headunit server shows the search while it runs`() {
        assertEquals(Status.SEARCHING, HomeStatusPolicy.status(true, Link.DISCONNECTED, scanning = true, sweepsWithoutPhone = 0))
    }

    @Test
    fun `after a sweep without the phone it says what to check, scanning or between sweeps`() {
        // The re-arm waits 10 s between sweeps; the message must not flicker back to "ready".
        assertEquals(Status.NOT_FOUND_YET, HomeStatusPolicy.status(true, Link.DISCONNECTED, scanning = true, sweepsWithoutPhone = 1))
        assertEquals(Status.NOT_FOUND_YET, HomeStatusPolicy.status(true, Link.DISCONNECTED, scanning = false, sweepsWithoutPhone = 3))
    }

    @Test
    fun `headunit server before any search is ready`() {
        assertEquals(Status.READY, HomeStatusPolicy.status(true, Link.DISCONNECTED, scanning = false, sweepsWithoutPhone = 0))
    }

    @Test
    fun `native mode never talks about a Wi-Fi search`() {
        assertEquals(Status.READY, HomeStatusPolicy.status(false, Link.DISCONNECTED, scanning = true, sweepsWithoutPhone = 2))
    }

    @Test
    fun `a connection wins over the search state`() {
        for (hus in listOf(true, false)) {
            assertEquals(Status.CONNECTING, HomeStatusPolicy.status(hus, Link.CONNECTING, scanning = true, sweepsWithoutPhone = 2))
            assertEquals(Status.CONNECTED, HomeStatusPolicy.status(hus, Link.CONNECTED, scanning = false, sweepsWithoutPhone = 2))
        }
    }

    @Test
    fun `a sweep counts as missed only when it ends with nothing connected`() {
        assertTrue(HomeStatusPolicy.isMissedSweep(wasScanning = true, scanning = false, link = Link.DISCONNECTED))
        // Found the phone: the sweep hands over to a connection.
        assertFalse(HomeStatusPolicy.isMissedSweep(wasScanning = true, scanning = false, link = Link.CONNECTING))
        // Not the end of a sweep.
        assertFalse(HomeStatusPolicy.isMissedSweep(wasScanning = false, scanning = false, link = Link.DISCONNECTED))
        assertFalse(HomeStatusPolicy.isMissedSweep(wasScanning = false, scanning = true, link = Link.DISCONNECTED))
    }
}
