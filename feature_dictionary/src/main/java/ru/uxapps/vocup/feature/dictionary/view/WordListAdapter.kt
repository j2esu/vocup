package ru.uxapps.vocup.feature.dictionary.view

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.dictionary.R
import ru.uxapps.vocup.feature.dictionary.databinding.ItemWordBinding
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.inflateBind

internal class WordListAdapter(
    private val onWordClick: (Word, View) -> Unit
) : ListAdapter<Word, WordListAdapter.WordVh>(object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.text == newItem.text
    override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WordVh(parent.inflateBind(ItemWordBinding::inflate))

    override fun onBindViewHolder(holder: WordVh, position: Int) = holder.bind(getItem(position))

    inner class WordVh(private val bind: ItemWordBinding) : ViewHolder(bind.root) {

        init {
            itemView.setOnClickListener { onWordClick(getItem(adapterPosition), it) }
        }

        fun bind(word: Word) = with(bind) {
            wordText.text = word.text
            wordTrans.apply {
                text = if (word.translations.isNotEmpty()) {
                    word.translations.joinToString(separator = ", ")
                } else {
                    getString(R.string.no_translations)
                }
                isEnabled = word.translations.isNotEmpty()
            }
            wordPron.apply {
                isVisible = word.pron != null
                text = getString(R.string.pron_pattern, word.pron)
            }
            wordProgress.setProgress(word.progress)
            root.transitionName = getString(R.string.transit_word_item_pattern, word.id)
        }
    }
}
