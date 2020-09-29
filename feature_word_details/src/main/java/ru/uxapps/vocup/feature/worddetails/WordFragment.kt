package ru.uxapps.vocup.feature.worddetails

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitValue
import ru.uxapps.vocup.feature.tts
import ru.uxapps.vocup.feature.worddetails.databinding.FragmentWordBinding
import ru.uxapps.vocup.feature.worddetails.di.WordViewModel
import ru.uxapps.vocup.feature.worddetails.model.WordDetails
import ru.uxapps.vocup.feature.worddetails.view.AddTransDialog
import ru.uxapps.vocup.feature.worddetails.view.EditTransDialog
import ru.uxapps.vocup.feature.worddetails.view.WordView
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class WordFragment : BaseFragment(R.layout.fragment_word), AddTransDialog.Host, EditTransDialog.Host {

    interface Router {
        fun onWordDeleted(undo: suspend () -> Unit)
    }

    companion object {
        fun argsOf(wordId: Long) = bundleOf("word" to wordId)
        private val WordFragment.wordId get() = requireArguments()["word"] as Long
    }

    private val vm by viewModels<WordViewModel>()

    @Inject internal lateinit var detailsModel: WordDetails

    override fun onViewReady(view: View, init: Boolean) {
        vm.getWordComponent(wordId).inject(this)
        val v = WordView(FragmentWordBinding.bind(view), object : WordView.Callback {

            override fun onDelete() = detailsModel.onDeleteWord()
            override fun onAddTrans() = AddTransDialog().show(childFragmentManager, null)
            override fun onDeleteTrans(trans: String) = detailsModel.onDeleteTrans(trans)
            override fun onReorderTrans(newTrans: List<String>) = detailsModel.onReorderTrans(newTrans)

            override fun onListen() {
                detailsModel.text.value?.let { tts.speak(it) }
            }

            override fun onEditTrans(trans: String) {
                EditTransDialog().apply { arguments = EditTransDialog.argsOf(trans) }
                    .show(childFragmentManager, null)
            }
        })
        with(detailsModel) {
            translations.observe(viewLifecycleOwner, v::setTranslations)
            text.observe(viewLifecycleOwner, v::setWordText)
            pron.observe(viewLifecycleOwner, v::setPronText)
            onTransDeleted.consume(viewLifecycleOwner) {
                v.showDeleteTransUndo { lifecycleScope.launchWhenStarted { it() } }
            }
            onWordDeleted.consume(viewLifecycleOwner) {
                host<Router>().onWordDeleted(it)
            }
        }
        postponeUntil {
            detailsModel.translations.awaitValue()
            detailsModel.text.awaitValue()
            detailsModel.pron.awaitValue()
        }
    }

    override fun onAddTrans(text: String) = detailsModel.onAddTrans(text)
    override fun onEditTrans(trans: String, newText: String) = detailsModel.onEditTrans(trans, newText)
    override fun onDeleteTrans(trans: String) = detailsModel.onDeleteTrans(trans)
}