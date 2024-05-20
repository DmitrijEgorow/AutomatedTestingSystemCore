package ru.myitschool.lab23.core

import android.content.ClipData
import android.content.ClipboardManager
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

interface PasteTextHandler {
    fun pasteText(mClipboardManager: ClipboardManager, typedText: String) {
        mClipboardManager.setPrimaryClip(
            ClipData.newPlainText(
                "Info",
                typedText,
            ),
        )
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK)
    }
}
