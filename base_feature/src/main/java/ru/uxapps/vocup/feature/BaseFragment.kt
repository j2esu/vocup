package ru.uxapps.vocup.feature

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    private val onReadyActions = mutableSetOf<() -> Unit>()
    private var savedViewState: Map<Int, SparseArray<Parcelable>>? = null

    private var hasPostponedTransition = false

    fun doOnReadyForTransition(action: () -> Unit) {
        if (view != null && !hasPostponedTransition) {
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

    override fun postponeEnterTransition() {
        super.postponeEnterTransition()
        hasPostponedTransition = true
    }

    override fun startPostponedEnterTransition() {
        restoreViewState()
        super.startPostponedEnterTransition()
        hasPostponedTransition = false
        notifyReadyForTransition()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        onViewReady(requireView(), savedInstanceState == null && savedViewState == null)
        if (!hasPostponedTransition) {
            restoreViewState()
            notifyReadyForTransition()
        }
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