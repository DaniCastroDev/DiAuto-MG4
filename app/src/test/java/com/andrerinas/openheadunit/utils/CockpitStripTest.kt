package com.andrerinas.openheadunit.utils

import com.andrerinas.openheadunit.utils.CockpitStrip.Side
import com.andrerinas.openheadunit.utils.CockpitStrip.Strip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CockpitStripTest {

    @Test
    fun `a strip on the left puts Android Auto on the right`() {
        assertEquals(640 to 0, CockpitStrip.horizontalInsets(0, 0, Strip(Side.LEFT, 640)))
    }

    @Test
    fun `a strip on the right puts Android Auto on the left`() {
        assertEquals(0 to 640, CockpitStrip.horizontalInsets(0, 0, Strip(Side.RIGHT, 640)))
    }

    @Test
    fun `the strip replaces a margin left over from placing it by hand`() {
        // The user had typed 640 on the left, then moved the strip right in MG4Dash.
        assertEquals(0 to 640, CockpitStrip.horizontalInsets(640, 0, Strip(Side.RIGHT, 640)))
    }

    @Test
    fun `without the strip the manual insets apply`() {
        assertEquals(30 to 50, CockpitStrip.horizontalInsets(30, 50, null))
    }

    @Test
    fun `the row MG4Dash publishes is read`() {
        assertEquals(Strip(Side.LEFT, 640), CockpitStrip.fromRow(enabled = 1, side = "left", widthPx = 640))
        assertEquals(Strip(Side.RIGHT, 640), CockpitStrip.fromRow(enabled = 1, side = "right", widthPx = 640))
    }

    @Test
    fun `a strip that is off, unknown or empty counts as no strip`() {
        assertNull(CockpitStrip.fromRow(enabled = 0, side = "left", widthPx = 640))
        assertNull(CockpitStrip.fromRow(enabled = 1, side = "top", widthPx = 640))
        assertNull(CockpitStrip.fromRow(enabled = 1, side = null, widthPx = 640))
        assertNull(CockpitStrip.fromRow(enabled = 1, side = "left", widthPx = 0))
    }
}
