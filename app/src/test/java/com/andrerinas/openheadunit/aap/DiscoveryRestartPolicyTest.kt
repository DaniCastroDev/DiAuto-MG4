package com.andrerinas.openheadunit.aap

import com.andrerinas.openheadunit.aap.DiscoveryRestartPolicy.Rearm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryRestartPolicyTest {

    @Test
    fun `the loop scans when nothing is in the way`() {
        assertEquals(Rearm.SCAN, DiscoveryRestartPolicy.rearm(serverUp = true, connectionBusy = false, userExited = false))
    }

    @Test
    fun `the loop waits out a connection instead of ending`() {
        // A connection being set up may still fail its handshake; the loop has to outlive it.
        assertEquals(Rearm.WAIT, DiscoveryRestartPolicy.rearm(serverUp = true, connectionBusy = true, userExited = false))
    }

    @Test
    fun `a user exit ends the loop instead of finding the phone again`() {
        // The phone's head unit server keeps listening after the user leaves; scanning would put
        // the projection straight back on screen.
        assertEquals(
            Rearm.STOP_USER_EXIT,
            DiscoveryRestartPolicy.rearm(serverUp = true, connectionBusy = false, userExited = true)
        )
    }

    @Test
    fun `an exit flag from the previous session does not end the wait for a live one`() {
        assertEquals(Rearm.WAIT, DiscoveryRestartPolicy.rearm(serverUp = true, connectionBusy = true, userExited = true))
    }

    @Test
    fun `the loop ends when the wireless server is gone, whatever else holds`() {
        for (busy in listOf(true, false)) for (exited in listOf(true, false)) {
            assertEquals(
                Rearm.STOP_SERVER_GONE,
                DiscoveryRestartPolicy.rearm(serverUp = false, connectionBusy = busy, userExited = exited)
            )
        }
    }

    @Test
    fun `a probe that never became a session is undone but not retried from here`() {
        // onConnected() ran, so the teardown must too; the retry belongs to the scan loop, whose
        // backoff a silent phone server needs.
        val handling = DiscoveryRestartPolicy.onDisconnected(hadSession = false, connectedSinceLastDisconnect = true)
        assertTrue(handling.teardown)
        assertFalse(handling.scheduleReconnect)
    }

    @Test
    fun `a disconnect after a session is undone and may reconnect`() {
        for (connected in listOf(true, false)) {
            val handling = DiscoveryRestartPolicy.onDisconnected(hadSession = true, connectedSinceLastDisconnect = connected)
            assertTrue(handling.teardown)
            assertTrue(handling.scheduleReconnect)
        }
    }

    @Test
    fun `the replayed disconnect at startup does nothing`() {
        // StateFlow replays Disconnected to a new collector before anything has connected.
        val handling = DiscoveryRestartPolicy.onDisconnected(hadSession = false, connectedSinceLastDisconnect = false)
        assertFalse(handling.teardown)
        assertFalse(handling.scheduleReconnect)
    }
}
