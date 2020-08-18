package ru.uxapps.vocup.screen.dict

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentDictBinding
import ru.uxapps.vocup.screen.SwipeDismissDecor

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
            val swipeDecor = SwipeDismissDecor(context.getDrawable(R.drawable.delete_item_hint_bg)!!) {
                callback.onSwipe(listAdapter.currentList[it.adapterPosition])
            }
            addItemDecoration(swipeDecor.also { it.attachToRecyclerView(this) })
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