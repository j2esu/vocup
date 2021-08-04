package ru.uxapps.vocup.feature.dictionary.view

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.SwipeDismissDecor
import ru.uxapps.vocup.feature.dictionary.R
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentDictBinding

internal class DictView(
    private val bind: FragmentDictBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onAdd(srcView: View)
        fun onSwipe(word: Word)
        fun onClick(word: Word, srcView: View)
    }

    private val listAdapter = WordListAdapter(callback::onClick)

    init {
        bind.dictAdd.setOnClickListener { callback.onAdd(it) }
        bind.dictList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
            val swipeDecor =
                SwipeDismissDecor(AppCompatResources.getDrawable(context, R.drawable.delete_item_hint_bg)!!) {
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
