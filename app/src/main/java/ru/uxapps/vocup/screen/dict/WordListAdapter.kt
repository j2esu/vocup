package ru.uxapps.vocup.screen.dict

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.ItemWordBinding
import ru.uxapps.vocup.util.inflateBind

class WordListAdapter(
    private val onWordClick: (ru.uxapps.vocup.data.Word) -> Unit
) : ListAdapter<ru.uxapps.vocup.data.Word, WordListAdapter.WordVh>(object : DiffUtil.ItemCallback<ru.uxapps.vocup.data.Word>() {
    override fun areItemsTheSame(oldItem: ru.uxapps.vocup.data.Word, newItem: ru.uxapps.vocup.data.Word) = oldItem.text == newItem.text
    override fun areContentsTheSame(oldItem: ru.uxapps.vocup.data.Word, newItem: ru.uxapps.vocup.data.Word) = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WordVh(parent.inflateBind(ItemWordBinding::inflate))

    override fun onBindViewHolder(holder: WordVh, position: Int) = holder.bind(getItem(position))

    inner class WordVh(private val bind: ItemWordBinding) : ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener { onWordClick(getItem(adapterPosition)) }
        }

        fun bind(word: ru.uxapps.vocup.data.Word) = with(bind) {
            wordText.text = word.text
            wordTrans.apply {
                text = if (word.translations.isNotEmpty()) {
                    word.translations.joinToString(separator = ", ")
                } else {
                    context.getString(R.string.no_translations)
                }
                isEnabled = word.translations.isNotEmpty()
            }
            wordPron.apply {
                isVisible = word.pron != null
                text = context.getString(R.string.pron_pattern, word.pron)
            }
            wordProgress.text = word.progress.toString()
        }
    }
}