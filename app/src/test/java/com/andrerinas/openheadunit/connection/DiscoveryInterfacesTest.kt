package com.andrerinas.openheadunit.connection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryInterfacesTest {

    @Test
    fun `the MG4 telematics and modem links are not swept`() {
        for (name in listOf("tbox0", "rmnet_data0", "ccmni1", "dummy0")) {
            assertFalse(name, DiscoveryInterfaces.isScannable(name))
        }
    }

    @Test
    fun `links that can lead to the phone are swept`() {
        // Ethernet and USB tethering stay in: other head units reach the phone over them.
        for (name in listOf("ap0", "wlan0", "swlan0", "softap0", "eth0", "rndis0", "p2p-wlan0-0")) {
            assertTrue(name, DiscoveryInterfaces.isScannable(name))
        }
    }

    @Test
    fun `the hotspot comes first even when the telematics link is enumerated before it`() {
        // As enumerated on the MG4 SWI68: tbox0 (index 7) before ap0 (index 9).
        val interfaces = listOf("tbox0", "wlan0", "ap0")
        assertEquals(listOf("ap0", "wlan0"), DiscoveryInterfaces.prioritize(interfaces) { it })
    }

    @Test
    fun `the order of the other links is kept`() {
        val interfaces = listOf("eth0", "wlan0", "rndis0")
        assertEquals(interfaces, DiscoveryInterfaces.prioritize(interfaces) { it })
    }
}
