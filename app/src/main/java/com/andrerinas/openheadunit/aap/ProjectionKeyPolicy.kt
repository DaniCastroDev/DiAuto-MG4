package com.andrerinas.openheadunit.aap

import android.view.KeyEvent

object ProjectionKeyPolicy {

    fun shouldRouteBackKeyToProjection(actionToPhysicalKeyCode: Map<Int, Int>, keyCode: Int): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK &&
                actionToPhysicalKeyCode.containsValue(KeyEvent.KEYCODE_BACK)
    }

    // The MG4's steering-wheel arrows already switch station etc. in the car; forwarded too, they
    // drop Android Auto into rotary focus mode and highlight whatever is on screen.
    private val NAVIGATION_KEYS = setOf(
        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER,
        KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_TAB
    )

    // Volume belongs to the car (SystemUI's volume handler); sent to Android Auto as well, any key it
    // receives also switches it to rotary focus mode and the highlight appears on every press.
    private val VOLUME_KEYS = setOf(
        KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_MUTE
    )

    /**
     * Whether a key from the car should go on to Android Auto. Navigation and volume keys only when
     * the user learned them in the keymap. Applied where every key path meets (CommManager.sendKey),
     * not only on the projection screen: broadcasts from the car reach Android Auto without it.
     */
    fun shouldForwardCarKey(actionToPhysicalKeyCode: Map<Int, Int>, keyCode: Int): Boolean =
        (keyCode !in NAVIGATION_KEYS && keyCode !in VOLUME_KEYS) || actionToPhysicalKeyCode.containsValue(keyCode)
}
