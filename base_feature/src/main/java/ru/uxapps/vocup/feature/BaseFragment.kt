package ru.uxapps.vocup.feature

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import ru.uxapps.vocup.util.forEachParentRecursive

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    companion object {
        private const val STATE_POSTPONED = "STATE_POSTPONED"
    }

    private val onReadyActions = mutableSetOf<() -> Unit>()
    private var savedViewState: Map<Int, SparseArray<Parcelable>>? = null
    private var postponeRequested = false
    private var postponeUntilFunctions = mutableSetOf<suspend () -> Unit>()

    protected var postponeTimeout = 500L

    fun postpone() {
        postponeRequested = true
    }

    private fun isPostponeNeeded(): Boolean {
        if (!postponeRequested) {
            var parentPostponeRequested = false
            forEachParentRecursive {
                if ((it as? BaseFragment)?.postponeRequested == true) {
                    parentPostponeRequested = true
                }
            }
            return parentPostponeRequested
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            postponeRequested = savedInstanceState.getBoolean(STATE_POSTPONED)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_POSTPONED, postponeRequested)
    }

    fun doOnReadyForTransition(action: () -> Unit) {
        if (view != null && !postponeRequested) {
            action()
        } else {
            onReadyActions.add(action)
        }
    }

    protected abstract fun onViewReady(view: View, init: Boolean)

    protected open fun getViewsSavedForTransition(): IntArray? = null

    private fun notifyReadyForTransition() {
        onReadyActions.forEach { it() }
        onReadyActions.clear()
    }

    private fun restoreViewState() {
        savedViewState?.forEach { (id, state) ->
            view?.findViewById<View>(id)?.restoreHierarchyState(state)
        }
    }

    override fun postponeEnterTransition(): Nothing {
        error("Use postpone() to request postponed transition")
    }

    override fun startPostponedEnterTransition(): Nothing {
        error("Use postponeUntil() to delay transition")
    }

    private fun postponeEnterTransitionInner() {
        super.postponeEnterTransition()
    }

    private fun startPostponedEnterTransitionInner() {
        restoreViewState()
        super.startPostponedEnterTransition()
        postponeRequested = false
        notifyReadyForTransition()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        postponeUntilFunctions.clear()
        if (isPostponeNeeded()) {
            postponeEnterTransitionInner()
            childFragmentManager.fragments.mapNotNull { it as BaseFragment }.forEach {
                postponeUntil { it.awaitReady() }
            }
        }
        onViewReady(requireView(), savedInstanceState == null && savedViewState == null)
        if (!isPostponeNeeded()) {
            restoreViewState()
            notifyReadyForTransition()
        } else if (postponeUntilFunctions.isEmpty()) {
            startPostponedEnterTransitionInner()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                withTimeoutOrNull(postponeTimeout) {
                    postponeUntilFunctions.forEach { it() }
                    delay(100) // workaround to let things like async submit list finish
                }
                (view?.parent as? ViewGroup)?.let {
                    it.doOnPreDraw {
                        startPostponedEnterTransitionInner()
                    }
                    it.invalidate()
                }
            }
        }
    }

    protected fun postponeUntil(await: suspend () -> Unit) {
        postponeUntilFunctions.add(await)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        savedViewState = getViewsSavedForTransition()?.associateWith { id ->
            SparseArray<Parcelable>().also {
                view?.findViewById<View>(id)?.saveHierarchyState(it)
            }
        } ?: emptyMap()
    }
}