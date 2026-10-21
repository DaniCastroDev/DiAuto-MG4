package com.andrerinas.openheadunit.main

/**
 * What the home screen says about the connection.
 *
 * In Headunit Server mode the app looks for the phone over Wi-Fi with nothing on screen to show for
 * it: a phone not on the car's hotspot, or with Android Auto's head unit server off, looked like a
 * Connect button that did nothing. The status line now follows the search, and once a full sweep
 * has gone by without the phone it says what to check while the search carries on.
 */
object HomeStatusPolicy {

    enum class Link { DISCONNECTED, CONNECTING, CONNECTED }

    enum class Status {
        /** Native AA, or Headunit Server before any search. */
        READY,
        /** Headunit Server: a sweep for the phone is running and none has finished yet. */
        SEARCHING,
        /** Headunit Server: at least one sweep finished without the phone; still looking. */
        NOT_FOUND_YET,
        CONNECTING,
        CONNECTED
    }

    fun status(headunitServer: Boolean, link: Link, scanning: Boolean, sweepsWithoutPhone: Int): Status = when {
        link == Link.CONNECTED -> Status.CONNECTED
        link == Link.CONNECTING -> Status.CONNECTING
        !headunitServer -> Status.READY
        sweepsWithoutPhone > 0 -> Status.NOT_FOUND_YET
        scanning -> Status.SEARCHING
        else -> Status.READY
    }

    /** A sweep just ended and nothing connected: the phone was not found on this pass. */
    fun isMissedSweep(wasScanning: Boolean, scanning: Boolean, link: Link): Boolean =
        wasScanning && !scanning && link == Link.DISCONNECTED
}
