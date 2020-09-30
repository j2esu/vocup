package ru.uxapps.vocup.feature.addword

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.addword.databinding.FragmentAddListBinding
import ru.uxapps.vocup.feature.addword.model.AddList
import ru.uxapps.vocup.feature.addword.model.AddListImp
import ru.uxapps.vocup.feature.addword.model.DefItem
import ru.uxapps.vocup.feature.addword.view.AddListView
import ru.uxapps.vocup.util.host

class AddListFragment : BaseFragment(R.layout.fragment_add_list) {

    interface Router {
        fun openWord(wordId: Long, srcView: View)
    }

    companion object {
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_LIST = "ARG_LIST"

        fun argsOf(title: String, list: List<String>) = bundleOf(ARG_TITLE to title, ARG_LIST to ArrayList(list))

        private val AddListFragment.title: String get() = requireArguments()[ARG_TITLE] as String

        @Suppress("UNCHECKED_CAST")
        private val AddListFragment.list: List<String>
            get() = requireArguments()[ARG_LIST] as List<String>
    }

    private lateinit var addListModel: AddList

    override fun onViewReady(view: View, init: Boolean) {
        addListModel = AddListImp(list, lifecycleScope, (activity?.application as RepoProvider).provideRepo())
        val addListView = AddListView(FragmentAddListBinding.bind(view), object : AddListView.Callback {
            override fun onOpen(item: DefItem, srcView: View) {
                item.wordId?.let { host<Router>().openWord(it, srcView) }
            }

            override fun onSave(item: DefItem) = addListModel.onSave(item)
            override fun onLangClick(lang: Language) = addListModel.onChooseLang(lang)
            override fun onRetry() = addListModel.onRetry()
        })
        addListView.setTitle(title)
        with(addListModel) {
            state.observe(viewLifecycleOwner, addListView::setState)
            languages.observe(viewLifecycleOwner, addListView::setLanguages)
        }
    }
}