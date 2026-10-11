package com.andrerinas.openheadunit.aap

/**
 * Hostnames Android (and OEM skins) probe to decide whether Wi‑Fi "has internet".
 *
 * SoftAP on the head unit has no upstream — after a while phones mark the network as broken and
 * drop it, which kills wireless Android Auto. Answering those probes locally with HTTP 204 (and
 * DNS A → SoftAP gateway) keeps the join up. Real app traffic still fails on Wi‑Fi; phones that
 * keep mobile data active continue to use cellular for Maps/music (same split dongles rely on).
 */
object SoftApCaptivePortalPolicy {

    /** UDP port we listen on; iptables REDIRECTs SoftAP DNS (53) here. Avoids clashing with mDNS 5353. */
    const val DNS_REDIRECT_PORT = 18053

    val SPOOFED_HOSTS: Set<String> = setOf(
        "connectivitycheck.gstatic.com",
        "connectivitycheck.android.com",
        "clients3.google.com", // older generate_204 probe
        "www.google.cn", // CN OEM skins
    )

    fun shouldSpoofHost(host: String?): Boolean {
        if (host.isNullOrBlank()) return false
        val h = host.trim().lowercase().trimEnd('.')
        return h in SPOOFED_HOSTS || SPOOFED_HOSTS.any { h.endsWith(".$it") }
    }

    /** Paths that mean "empty 204 = online" on Android's captive portal detector. */
    fun shouldAnswer204(path: String?): Boolean {
        if (path.isNullOrBlank()) return true
        val p = path.lowercase()
        return p.contains("generate_204") ||
            p.contains("gen_204") ||
            p == "/" ||
            p.startsWith("/?") ||
            p.contains("captive")
    }
}
