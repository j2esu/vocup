package ru.uxapps.vocup.feature.dictionary.screen.word

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.feature.dictionary.R
import ru.uxapps.vocup.feature.dictionary.screen.common.SwipeDismissDecor
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentWordBinding
import ru.uxapps.vocup.feature.setNavAsBack

class WordView(
    private val bind: FragmentWordBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onDelete()
        fun onReorderTrans(newTrans: List<String>)
        fun onAddTrans()
        fun onEditTrans(trans: String)
        fun onDeleteTrans(trans: String)
        fun onListen()
    }

    private val transAdapter = TransListAdapter(callback::onReorderTrans, callback::onEditTrans)

    init {
        bind.wordToolbar.apply {
            setNavAsBack()
            menu.findItem(R.id.menu_word_del).setOnMenuItemClickListener {
                callback.onDelete()
                true
            }
        }
        bind.wordTransList.apply {
            adapter = transAdapter
            layoutManager = LinearLayoutManager(context)
            val swipeDecor =
                SwipeDismissDecor(context.getDrawable(R.drawable.delete_item_hint_bg)!!) {
                    callback.onDeleteTrans(transAdapter.currentList[it.adapterPosition])
                }
            addItemDecoration(swipeDecor.also { it.attachToRecyclerView(this) })
        }
        bind.wordAddTrans.setOnClickListener { callback.onAddTrans() }
        bind.wordPron.setOnClickListener { callback.onListen() }
    }

    fun setTranslations(trans: List<String>?) = with(bind) {
        wordTransProgress.isVisible = trans == null
        if (trans != null) {
            transAdapter.submitList(trans)
        }
    }

    fun setWordText(text: String) = with(bind) {
        wordText.text = text
    }

    @SuppressLint("SetTextI18n")
    fun setPronText(text: String) = with(bind) {
        if (text.isNotBlank()) {
            wordPron.text = "/$text/"
        } else {
            wordPron.setText(R.string.listen_pron)
        }
    }

    fun showDeleteTransUndo(undo: () -> Unit) = with(bind) {
        Snackbar.make(root, R.string.translation_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { undo() }
            .show()
    }
}