package com.andrerinas.openheadunit.utils

/**
 * The screen mode DiAuto starts in.
 *
 * Immersive, both SAIC bars hidden, so Android Auto gets the whole 1920x720 panel. The MG4 build
 * used to default to "hide left bar only", which keeps the SAIC top strip and reserves
 * [OemDisplayChrome.TOP_BAR_PX] for it: the projection was 1920x640, short of full screen
 * (owner's report, 2026-10-22). The SAIC SystemUI honours the standard immersive flags, the same
 * ones MG4Dash uses to hide both bars.
 */
object ScreenModeDefault {
    val DEFAULT = Settings.FullscreenMode.IMMERSIVE

    /**
     * The value to store on the one-time move to the new default, or null to leave it. Only the old
     * default is replaced: nothing stored, or any mode other than "hide left bar only", was either
     * never set or chosen on purpose.
     */
    fun migrate(stored: Int?): Int? =
        if (stored == Settings.FullscreenMode.NAV_ONLY.value) DEFAULT.value else null
}
