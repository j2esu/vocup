package ru.uxapps.vocup.feature.dictionary

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.SwipeDismissDecor
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentDictBinding

class DictView(
    private val bind: FragmentDictBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onAdd()
        fun onSwipe(word: Word)
        fun onClick(word: Word)
    }

    private val listAdapter = WordListAdapter(callback::onClick)

    init {
        bind.dictAdd.setOnClickListener { callback.onAdd() }
        bind.dictList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val swipeDecor =
                SwipeDismissDecor(context.getDrawable(R.drawable.delete_item_hint_bg)!!) {
                    callback.onSwipe(listAdapter.currentList[it.adapterPosition])
                }
            addItemDecoration(swipeDecor.also { it.attachToRecyclerView(this) })
        }
    }

    fun setWords(words: List<Word>) = with(bind) {
        listAdapter.submitList(words)
        dictEmpty.isVisible = words.isEmpty()
    }

    fun setLoading(loading: Boolean) = with(bind) {
        dictProgress.isVisible = loading
    }

    fun showRemoveWordUndo(undo: () -> Unit) = with(bind) {
        Snackbar.make(root, R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { undo() }
            .show()
    }
}