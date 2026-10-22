package com.andrerinas.openheadunit.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScreenModeDefaultTest {

    @Test
    fun `DiAuto starts immersive, with both SAIC bars hidden`() {
        assertEquals(Settings.FullscreenMode.IMMERSIVE, ScreenModeDefault.DEFAULT)
    }

    @Test
    fun `the old hide-left-bar-only default moves to immersive once`() {
        assertEquals(Settings.FullscreenMode.IMMERSIVE.value, ScreenModeDefault.migrate(Settings.FullscreenMode.NAV_ONLY.value))
    }

    @Test
    fun `nothing stored or another mode chosen is left alone`() {
        assertNull(ScreenModeDefault.migrate(null))
        assertNull(ScreenModeDefault.migrate(Settings.FullscreenMode.NONE.value))
        assertNull(ScreenModeDefault.migrate(Settings.FullscreenMode.STATUS_ONLY.value))
        assertNull(ScreenModeDefault.migrate(Settings.FullscreenMode.IMMERSIVE_WITH_NOTCH.value))
    }
}
