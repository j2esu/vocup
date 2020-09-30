package ru.uxapps.vocup.feature.addword.view

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.feature.addword.databinding.FragmentAddListBinding
import ru.uxapps.vocup.feature.addword.model.AddList.State
import ru.uxapps.vocup.feature.addword.model.AddList.State.*
import ru.uxapps.vocup.feature.addword.model.DefItem
import ru.uxapps.vocup.feature.setNavAsBack

internal class AddListView(
    private val bind: FragmentAddListBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onOpen(item: DefItem, srcView: View)
        fun onSave(item: DefItem)
        fun onLangClick(lang: Language)
        fun onRetry()
    }

    private val listAdapter = DefListAdapter(callback::onOpen, callback::onSave)
    private val lang = LangView(bind.addListToolbar, callback::onLangClick)

    init {
        with(bind) {
            addListRetry.setOnClickListener { callback.onRetry() }
            addListToolbar.setNavAsBack()
            addListDefRv.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    fun setState(state: State) = with(bind) {
        addListProgress.isVisible = state is Loading
        addListErrorLayout.isVisible = state is Error
        listAdapter.submitList((state as? Data)?.items ?: emptyList())
    }

    fun setTitle(title: String) {
        bind.addListToolbar.title = title
    }

    fun setLanguages(languages: List<Language>) = lang.setLanguages(languages)
}