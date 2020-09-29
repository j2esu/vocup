package ru.uxapps.vocup.helper

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.uxapps.vocup.feature.SoftInput

class SoftInputImp(private val activity: FragmentActivity) : SoftInput {

    private val imm = activity.getSystemService<InputMethodManager>()

    override var nextShowDelay = 0L

    override fun show(view: View) {
        activity.lifecycleScope.launch {
            delay(nextShowDelay)
            view.clearFocus() // weird bug, without resetting focus keyboard not opens next time
            view.requestFocus()
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            nextShowDelay = 0L
        }
    }

    override fun hide() {
        activity.currentFocus?.windowToken?.let {
            imm?.hideSoftInputFromWindow(it, 0)
        }
    }
}