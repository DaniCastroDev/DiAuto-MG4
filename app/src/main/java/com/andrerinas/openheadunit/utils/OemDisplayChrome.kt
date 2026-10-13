package com.andrerinas.openheadunit.utils

/**
 * OEM chrome that Android's [androidx.core.view.WindowInsetsCompat.Type.statusBars]
 * often under-reports on SAIC AllGo (MG4 EH32). When the top strip stays visible, AA must
 * size to roughly 1920×(1080 − [TOP_BAR_PX]) or the stream fights the panel and glitches.
 */
object OemDisplayChrome {
    /** Measured top system strip on MG4 when the upper bar is shown. */
    const val TOP_BAR_PX = 80

    fun topBarReservePx(topBarVisible: Boolean): Int = if (topBarVisible) TOP_BAR_PX else 0
}
