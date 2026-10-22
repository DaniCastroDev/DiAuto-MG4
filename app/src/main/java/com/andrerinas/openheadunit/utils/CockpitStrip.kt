package com.andrerinas.openheadunit.utils

import android.content.Context
import android.net.Uri

/**
 * The MG4Dash cockpit strip, a 640 px panel MG4Dash draws over one side of the projection.
 *
 * When it is on, Android Auto has to fit the rest of the screen, on the other side. MG4Dash
 * publishes the strip (on or off, which side, how wide) through a read-only provider, so the
 * horizontal insets follow its "strip side" setting without the user mirroring it here by hand in
 * Custom Insets. Without MG4Dash, or with its strip off, the manual insets apply as before.
 */
object CockpitStrip {

    enum class Side { LEFT, RIGHT }

    data class Strip(val side: Side, val widthPx: Int)

    private const val URI = "content://app.mg4dash.strip/strip"

    /** The strip MG4Dash reports, or null when it is off, unknown or MG4Dash is not installed. */
    fun read(context: Context): Strip? {
        val (strip, outcome) = try {
            context.contentResolver.query(Uri.parse(URI), null, null, null, null)?.use { cursor ->
                if (!cursor.moveToFirst()) return@use null to "MG4Dash returned no row"
                val enabled = cursor.getInt(cursor.getColumnIndexOrThrow("enabled"))
                val side = cursor.getString(cursor.getColumnIndexOrThrow("side"))
                val width = cursor.getInt(cursor.getColumnIndexOrThrow("width"))
                fromRow(enabled, side, width) to "MG4Dash says enabled=$enabled side=$side width=$width"
            } ?: (null to "no MG4Dash strip provider")
        } catch (e: Exception) {
            // No provider (MG4Dash missing or older), or a refused permission: manual insets it is.
            null to "reading the MG4Dash strip failed: ${e.javaClass.simpleName} ${e.message}"
        }
        logOnChange(outcome)
        return strip
    }

    // read() runs on every insets pass; the log only needs to say when the answer changes.
    @Volatile private var lastOutcome: String? = null

    private fun logOnChange(outcome: String) {
        if (outcome == lastOutcome) return
        lastOutcome = outcome
        try { AppLog.i("CockpitStrip: $outcome") } catch (_: Throwable) {}
    }

    fun fromRow(enabled: Int, side: String?, widthPx: Int): Strip? {
        if (enabled == 0 || widthPx <= 0) return null
        val parsed = when (side) {
            "left" -> Side.LEFT
            "right" -> Side.RIGHT
            else -> return null
        }
        return Strip(parsed, widthPx)
    }

    /**
     * Left and right insets to use. The strip replaces both manual values rather than adding to
     * them: an inset left over from placing the strip by hand would otherwise squeeze Android Auto
     * from both sides once the strip changes side.
     */
    fun horizontalInsets(manualLeft: Int, manualRight: Int, strip: Strip?): Pair<Int, Int> = when (strip?.side) {
        null -> manualLeft to manualRight
        Side.LEFT -> strip.widthPx to 0
        Side.RIGHT -> 0 to strip.widthPx
    }
}
