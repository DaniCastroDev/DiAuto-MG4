package com.andrerinas.openheadunit.connection

/**
 * Which network interfaces [NetworkDiscovery] probes for the phone, and in which order.
 *
 * The phone is on a network this unit shares with it: the unit's own hotspot, or a Wi-Fi network
 * both have joined. A head unit also carries links that never lead to a phone — the MG4's
 * telematics box (`tbox0`, its own /24), modem data (`rmnet*`, `ccmni*`) — and every sweep of one
 * of those is 254 hosts × two ports of connection attempts into the car's internal network, every
 * ten seconds for as long as the phone is not there.
 *
 * Known non-LAN links are skipped rather than LAN ones allowed: other head units reach the phone
 * over Ethernet or USB tethering, and an allow-list would quietly stop finding it there.
 */
internal object DiscoveryInterfaces {

    private val NON_LAN_PREFIXES = listOf("tbox", "rmnet", "ccmni", "dummy")
    private val ACCESS_POINT_PREFIXES = listOf("ap", "swlan", "softap")

    fun isScannable(name: String): Boolean = NON_LAN_PREFIXES.none { name.startsWith(it) }

    fun isAccessPoint(name: String): Boolean = ACCESS_POINT_PREFIXES.any { name.startsWith(it) }

    /** Drops links that never lead to the phone; access points first, the rest in their order. */
    fun <T> prioritize(items: List<T>, interfaceName: (T) -> String): List<T> =
        items.filter { isScannable(interfaceName(it)) }
            .sortedBy { if (isAccessPoint(interfaceName(it))) 0 else 1 }
}
