package ru.uxapps.vocup.screen.word

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.ItemTransBinding
import ru.uxapps.vocup.util.inflateBinding
import java.util.*


class TransListAdapter(
    private val onReorder: (List<String>) -> Unit
) : ListAdapter<String, TransListAdapter.TransVh>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = true
    }
) {

    private var dragTransZ: Float = 0f

    private val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {

        private var prevList: List<String>? = null
        private var dragged: View? = null

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
            viewHolder.itemView.animate().cancel()
            viewHolder.itemView.translationZ = 0f
            viewHolder.itemView.isActivated = false
            super.clearView(recyclerView, viewHolder)
        }

        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                prevList = currentList
                dragged = viewHolder?.itemView
                dragged?.isActivated = true
                dragged?.animate()?.translationZ(dragTransZ)
            } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                dragged?.animate()?.translationZ(0f)
                dragged = null
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
        dragTransZ = recyclerView.resources.getDimension(R.dimen.trans_drag_z)
        recyclerView.addItemDecoration(touchHelper)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransVh(parent.inflateBinding(ItemTransBinding::inflate))

    override fun onBindViewHolder(holder: TransVh, position: Int) = holder.bind(getItem(position))

    @SuppressLint("ClickableViewAccessibility")
    inner class TransVh(private val binding: ItemTransBinding) : ViewHolder(binding.root) {

        init {
            binding.transDrag.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(this)
                }
                false
            }
            binding.root.setOnClickListener {
                // TODO: 8/17/2020 open edit
            }
        }

        fun bind(trans: String) = with(binding) {
            transText.text = trans
        }
    }
}