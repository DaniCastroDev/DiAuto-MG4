package com.andrerinas.openheadunit.aap

import com.andrerinas.openheadunit.connection.SoftApCaptivePortalSpoof
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SoftApCaptivePortalPolicyTest {

    @Test
    fun `spoofs only connectivity check hosts`() {
        assertTrue(SoftApCaptivePortalPolicy.shouldSpoofHost("connectivitycheck.gstatic.com"))
        assertTrue(SoftApCaptivePortalPolicy.shouldSpoofHost("CONNECTIVITYCHECK.GSTATIC.COM."))
        assertTrue(SoftApCaptivePortalPolicy.shouldSpoofHost("clients3.google.com"))
        assertFalse(SoftApCaptivePortalPolicy.shouldSpoofHost("www.google.com"))
        assertFalse(SoftApCaptivePortalPolicy.shouldSpoofHost("maps.googleapis.com"))
        assertFalse(SoftApCaptivePortalPolicy.shouldSpoofHost(null))
    }

    @Test
    fun `answers 204 for generate_204 paths`() {
        assertTrue(SoftApCaptivePortalPolicy.shouldAnswer204("/generate_204"))
        assertTrue(SoftApCaptivePortalPolicy.shouldAnswer204("/gen_204"))
        assertTrue(SoftApCaptivePortalPolicy.shouldAnswer204("/"))
        assertTrue(SoftApCaptivePortalPolicy.shouldAnswer204(null))
    }

    @Test
    fun `dns packet round trip returns gateway A record`() {
        // Minimal DNS query for connectivitycheck.gstatic.com
        val qname = "connectivitycheck.gstatic.com"
        val labels = qname.split('.')
        val nameBytes = ArrayList<Byte>()
        for (label in labels) {
            nameBytes.add(label.length.toByte())
            nameBytes.addAll(label.toByteArray(Charsets.US_ASCII).toList())
        }
        nameBytes.add(0)
        val header = byteArrayOf(
            0x12, 0x34, // id
            0x01, 0x00, // RD
            0x00, 0x01, // QDCOUNT
            0x00, 0x00,
            0x00, 0x00,
            0x00, 0x00
        )
        val questionTail = byteArrayOf(0x00, 0x01, 0x00, 0x01) // TYPE A CLASS IN
        val query = header + nameBytes.toByteArray() + questionTail

        assertEquals(qname, SoftApCaptivePortalSpoof.DnsPackets.parseQueryName(query, query.size))

        val gateway = byteArrayOf(192.toByte(), 168.toByte(), 43, 1)
        val response = SoftApCaptivePortalSpoof.DnsPackets.buildAResponse(query, query.size, gateway)
        assertNotNull(response)
        // Last 4 bytes are the A record address
        val r = response!!
        assertEquals(192.toByte(), r[r.size - 4])
        assertEquals(168.toByte(), r[r.size - 3])
        assertEquals(43.toByte(), r[r.size - 2])
        assertEquals(1.toByte(), r[r.size - 1])
        assertTrue((r[2].toInt() and 0x80) != 0) // QR
    }
}
