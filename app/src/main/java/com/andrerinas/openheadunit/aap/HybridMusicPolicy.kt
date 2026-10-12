package com.andrerinas.openheadunit.aap

/**
 * Hybrid AA: video/navigation over Wi‑Fi, music on the car Bluetooth A2DP path.
 *
 * MG4 stutters when media PCM rides the Wi‑Fi AA channel, so the MEDIA sink stays omitted
 * whenever the user enables this mode. Sound in the car then requires a live A2DP link —
 * Service Discovery asks [com.andrerinas.openheadunit.utils.BluetoothHelper] to reconnect it
 * and switches the HU source to Bluetooth rather than falling back to Wi‑Fi media.
 */
object HybridMusicPolicy {
    /** Omit the AA MEDIA sink whenever the hybrid preference is on. */
    fun omitAaMediaSink(musicViaBluetooth: Boolean): Boolean = musicViaBluetooth

    /** True when hybrid is on but the car has no confirmed A2DP media link yet. */
    fun needsBluetoothMediaLink(musicViaBluetooth: Boolean, a2dpLinkState: Boolean?): Boolean =
        musicViaBluetooth && a2dpLinkState != true
}
