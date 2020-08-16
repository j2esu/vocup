package ru.uxapps.vocup.screen.word

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentWordBinding

class WordView(
        private val binding: FragmentWordBinding,
        private val callback: Callback
) {

    private val transAdapter = TransListAdapter()

    init {
        with(binding) {
            wordToolbar.apply {
                setNavigationOnClickListener { callback.onUp() }
                menu.findItem(R.id.menu_word_del).setOnMenuItemClickListener {
                    callback.onDelete()
                    true
                }
            }
            wordTransList.apply {
                adapter = transAdapter
                layoutManager = LinearLayoutManager(context)
            }
            wordEditTrans.setOnClickListener {
                // TODO: 8/16/2020 handle edit action
            }
        }
    }

    fun setTranslations(trans: List<String>?) = with(binding) {
        wordTransProgress.isVisible = trans == null
        if (trans != null) {
            transAdapter.submitList(trans)
            // TODO: 8/16/2020 empty trans view
        }
    }

    fun setWordText(text: String) = with(binding) {
        wordText.text = text
    }

    interface Callback {
        fun onUp()
        fun onDelete()
    }
}