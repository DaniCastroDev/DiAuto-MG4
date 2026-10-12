package com.andrerinas.openheadunit.aap

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HybridMusicPolicyTest {

    @Test
    fun `hybrid omits aa media sink`() {
        assertTrue(HybridMusicPolicy.omitAaMediaSink(true))
        assertFalse(HybridMusicPolicy.omitAaMediaSink(false))
    }

    @Test
    fun `needs bluetooth warning when hybrid on and a2dp down or unknown`() {
        assertTrue(HybridMusicPolicy.needsBluetoothMediaLink(true, false))
        assertTrue(HybridMusicPolicy.needsBluetoothMediaLink(true, null))
        assertFalse(HybridMusicPolicy.needsBluetoothMediaLink(true, true))
        assertFalse(HybridMusicPolicy.needsBluetoothMediaLink(false, false))
    }
}
