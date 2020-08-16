package ru.uxapps.vocup.screen.dict

import android.graphics.Canvas
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentDictBinding
import kotlin.math.abs
import kotlin.math.round

class DictView(
        private val binding: FragmentDictBinding,
        private val callback: Callback
) {

    interface Callback {
        fun onAdd()
        fun onSwipe(word: Word)
        fun onClick(word: Word)
    }

    private val listAdapter = WordListAdapter(callback::onClick)

    init {
        binding.dictAdd.setOnClickListener { callback.onAdd() }
        binding.dictList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(ItemTouchHelper(object : ItemTouchHelper.Callback() {

                private val swipeBg = context.getDrawable(R.drawable.word_swipe_bg)!!

                override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder) =
                    makeMovementFlags(0, ItemTouchHelper.START or ItemTouchHelper.END)

                override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder) =
                    false

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    callback.onSwipe(listAdapter.currentList[viewHolder.adapterPosition])
                }

                override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder,
                        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    val item = viewHolder.itemView
                    val clipLeft = if (dX >= 0) 0 else item.width + dX.toInt()
                    val clipRight = if (dX >= 0) dX.toInt() else item.width
                    canvas.clipRect(clipLeft, item.top, clipRight, item.bottom)
                    swipeBg.setBounds(0, item.top, item.width, item.bottom)
                    swipeBg.alpha = round((1 - abs(dX / item.width)) * 255).toInt()
                    swipeBg.draw(canvas)
                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }).also { it.attachToRecyclerView(this) })
        }
    }

    fun setWords(words: List<Word>) = with(binding) {
        listAdapter.submitList(words)
        dictEmpty.isVisible = words.isEmpty()
    }

    fun setLoading(loading: Boolean) = with(binding) {
        dictProgress.isVisible = loading
    }

    fun showRemoveWordUndo(undo: () -> Unit) = with(binding) {
        Snackbar.make(root, R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { undo() }
            .show()
    }
}