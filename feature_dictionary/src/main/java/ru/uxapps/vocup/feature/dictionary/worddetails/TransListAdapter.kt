package ru.uxapps.vocup.feature.dictionary.worddetails

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.dictionary.databinding.ItemTransBinding
import ru.uxapps.vocup.feature.inflateBind
import java.util.*


class TransListAdapter(
    private val onReorder: (List<String>) -> Unit,
    private val onClick: (String) -> Unit
) : ListAdapter<String, TransListAdapter.TransVh>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = true
    }
) {

    private val dragDecor = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {

        private var prevList: List<String>? = null

        override fun onMove(rv: RecyclerView, vh: ViewHolder, target: ViewHolder): Boolean {
            currentList.toMutableList().apply {
                Collections.swap(this, vh.adapterPosition, target.adapterPosition)
                submitList(this)
            }
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
            prevList?.let {
                if (prevList != currentList) {
                    onReorder(currentList)
                }
                prevList = null
            }
            viewHolder.itemView.isActivated = false
            super.clearView(recyclerView, viewHolder)
        }

        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                prevList = currentList
                viewHolder?.itemView?.isActivated = true
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun isLongPressDragEnabled() = false

        override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder,
            dx: Float, dy: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
            // don't go outside rv
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.itemView.apply {
                    val topLimit = 0f
                    val bottomLimit = (recyclerView.height - height).toFloat()
                    if (!recyclerView.canScrollVertically(-1) && y < topLimit) {
                        y = topLimit
                    } else if (!recyclerView.canScrollVertically(1) && y > bottomLimit) {
                        y = bottomLimit
                    }
                }
            }
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            // TODO: 8/16/2020
        }
    })

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(dragDecor)
        dragDecor.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransVh(parent.inflateBind(ItemTransBinding::inflate))

    override fun onBindViewHolder(holder: TransVh, position: Int) = holder.bind(getItem(position))

    @SuppressLint("ClickableViewAccessibility")
    inner class TransVh(private val bind: ItemTransBinding) : ViewHolder(bind.root) {

        init {
            bind.transDrag.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    dragDecor.startDrag(this)
                }
                false
            }
            bind.root.setOnClickListener {
                onClick(getItem(adapterPosition))
            }
            // same functionality on long press
            bind.root.setOnLongClickListener {
                onClick(getItem(adapterPosition))
                true
            }
        }

        fun bind(trans: String) = with(bind) {
            transText.text = trans
        }
    }
}