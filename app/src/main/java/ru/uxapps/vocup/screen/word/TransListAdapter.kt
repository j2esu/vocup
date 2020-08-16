package ru.uxapps.vocup.screen.word

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.databinding.ItemTransBinding
import ru.uxapps.vocup.util.inflateBinding
import java.util.*


class TransListAdapter(
        private val onDrag: (from: Int, to: Int) -> Unit
) : ListAdapter<String, TransListAdapter.TransVh>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = true
    }
) {

    private val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {

        private var dragStartPos: Int? = null

        override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder,
                target: ViewHolder): Boolean {
            currentList.toMutableList().apply {
                Collections.swap(this, viewHolder.adapterPosition, target.adapterPosition)
                submitList(this)
            }
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
            dragStartPos?.let {
                val dragEndPos = viewHolder.adapterPosition
                if (it != dragEndPos) {
                    onDrag(it, dragEndPos)
                }
                dragStartPos = null
            }
            super.clearView(recyclerView, viewHolder)
        }

        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                dragStartPos = viewHolder!!.adapterPosition
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            // TODO: 8/16/2020
        }
    })

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
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
        }

        fun bind(trans: String) = with(binding) {
            transText.text = trans
        }
    }
}