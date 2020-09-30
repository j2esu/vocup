package ru.uxapps.vocup.feature.explore.view

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.feature.explore.R
import ru.uxapps.vocup.feature.explore.databinding.FragmentExploreBinding
import ru.uxapps.vocup.feature.explore.model.Explore.State
import ru.uxapps.vocup.feature.explore.model.Explore.State.*
import ru.uxapps.vocup.feature.explore.model.KitItem

internal class ExploreView(
    private val bind: FragmentExploreBinding,
    callback: Callback
) {

    interface Callback {
        fun onRetry()
        fun onClick(item: KitItem, srcView: View)
        fun onSwipe(item: KitItem)
    }

    private val listAdapter = KitListAdapter(callback::onClick)

    init {
        with(bind) {
            exploreRetry.setOnClickListener { callback.onRetry() }
            exploreRv.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context)
                val swipeDismiss = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                    0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onSwiped(viewHolder: ViewHolder, direction: Int) =
                        callback.onSwipe(listAdapter.currentList[viewHolder.adapterPosition])

                    override fun onMove(rv: RecyclerView, vh: ViewHolder, target: ViewHolder) = false
                })
                addItemDecoration(swipeDismiss.also { it.attachToRecyclerView(this) })
            }
        }
    }

    fun setState(state: State) = with(bind) {
        exploreProgress.isVisible = state is Loading
        exploreErrorLayout.isVisible = state is Error
        exploreEmpty.isVisible = state is Data && state.kits.isEmpty()
        listAdapter.submitList((state as? Data)?.kits ?: emptyList())
    }

    fun showDismissKitUndo(undo: () -> Unit) {
        Snackbar.make(bind.root, R.string.kit_was_dismissed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { undo() }
            .show()
    }
}