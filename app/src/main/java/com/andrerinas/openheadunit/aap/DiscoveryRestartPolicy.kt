package com.andrerinas.openheadunit.aap

/**
 * When the head unit goes looking for the phone again on its own, without the user asking.
 *
 * Two loops can restart discovery: the re-arm that follows every finished scan, and
 * [AapService]'s reconnect after a disconnect. Both have to agree on the same two rules, which is
 * why they live here rather than inline:
 *
 * - A user exit ends it. Android Auto's head unit server keeps listening on the phone after the
 *   user leaves, so a scan that runs anyway finds it at once and puts the projection back on
 *   screen. Only something that says the user wants it again (the Connect button, the phone coming
 *   back over Bluetooth) restarts the loop.
 * - A connection that never became a session is paced by the scan loop alone. The reconnect path
 *   retries every 2 s on a small budget and then tells the user it gave up; a phone server that
 *   accepts and stays silent would spend that budget in seconds and skip the backoff in
 *   [UnresponsivePeerPolicy], which exists for exactly that phone.
 */
object DiscoveryRestartPolicy {

    enum class Rearm {
        /** A connection is live or being set up: check again later. */
        WAIT,
        /** Nothing in the way: scan. */
        SCAN,
        /** The wireless server was stopped; the loop has nothing to serve. */
        STOP_SERVER_GONE,
        /** The user left Android Auto; stay quiet until they ask for it again. */
        STOP_USER_EXIT
    }

    /**
     * What the re-arm after a finished scan does next.
     *
     * The busy check comes before the user-exit check on purpose: the flag is set by the
     * disconnect that ends a session, so while that session is still up it describes the
     * previous one.
     */
    fun rearm(serverUp: Boolean, connectionBusy: Boolean, userExited: Boolean): Rearm = when {
        !serverUp -> Rearm.STOP_SERVER_GONE
        connectionBusy -> Rearm.WAIT
        userExited -> Rearm.STOP_USER_EXIT
        else -> Rearm.SCAN
    }

    /**
     * What a [CommManager.ConnectionState.Disconnected] has to undo, and whether it may schedule
     * a reconnect.
     *
     * @param hadSession a session reached TransportStarted since the service started.
     * @param connectedSinceLastDisconnect a TCP connection was made since the last disconnect,
     *   so onConnected() ran (radio paused, WifiLock, GPS…) and has to be undone even if that
     *   connection never became a session.
     */
    fun onDisconnected(hadSession: Boolean, connectedSinceLastDisconnect: Boolean): DisconnectHandling =
        DisconnectHandling(
            teardown = hadSession || connectedSinceLastDisconnect,
            scheduleReconnect = hadSession
        )

    data class DisconnectHandling(val teardown: Boolean, val scheduleReconnect: Boolean)
}
