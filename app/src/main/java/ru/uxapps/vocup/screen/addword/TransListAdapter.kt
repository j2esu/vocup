package ru.uxapps.vocup.screen.addword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.uxapps.vocup.data.Definition
import ru.uxapps.vocup.databinding.ItemTransBinding

class TransListAdapter : ListAdapter<Definition, TransListAdapter.TransVh>(
    object : DiffUtil.ItemCallback<Definition>() {
        override fun areItemsTheSame(oldItem: Definition, newItem: Definition) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Definition, newItem: Definition) = true
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransVh {
        return TransVh(ItemTransBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TransVh, position: Int) = holder.bind(getItem(position))

    class TransVh(private val binding: ItemTransBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(def: Definition) = with(binding) {
            transText.text = "${def.text} (${def.partOfSpeech})"
            transTranscription.text = def.transcription
            transTranslation.text = def.translations.joinToString { it.text }
        }
    }
}