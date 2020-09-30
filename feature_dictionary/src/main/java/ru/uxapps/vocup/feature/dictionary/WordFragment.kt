package ru.uxapps.vocup.feature.dictionary

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitValue
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentWordBinding
import ru.uxapps.vocup.feature.tts
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class WordFragment : BaseFragment(R.layout.fragment_word),
    ru.uxapps.vocup.feature.dictionary.view.AddTransDialog.Host,
    ru.uxapps.vocup.feature.dictionary.view.EditTransDialog.Host {

    interface Router {
        fun onWordDeleted(undo: suspend () -> Unit)
    }

    companion object {
        fun argsOf(wordId: Long) = bundleOf("word" to wordId)
        private val WordFragment.wordId get() = requireArguments()["word"] as Long
    }

    private val vm by viewModels<ru.uxapps.vocup.feature.dictionary.di.WordViewModel>()

    @Inject internal lateinit var detailsModel: ru.uxapps.vocup.feature.dictionary.model.WordDetails

    override fun onViewReady(view: View, init: Boolean) {
        vm.getWordComponent(wordId).inject(this)
        val v = ru.uxapps.vocup.feature.dictionary.view.WordView(
            FragmentWordBinding.bind(view),
            object : ru.uxapps.vocup.feature.dictionary.view.WordView.Callback {

                override fun onDelete() = detailsModel.onDeleteWord()
                override fun onAddTrans() =
                    ru.uxapps.vocup.feature.dictionary.view.AddTransDialog().show(childFragmentManager, null)

                override fun onDeleteTrans(trans: String) = detailsModel.onDeleteTrans(trans)
                override fun onReorderTrans(newTrans: List<String>) = detailsModel.onReorderTrans(newTrans)

                override fun onListen() {
                    detailsModel.text.value?.let { tts.speak(it) }
                }

                override fun onEditTrans(trans: String) {
                    ru.uxapps.vocup.feature.dictionary.view.EditTransDialog().apply {
                        arguments = ru.uxapps.vocup.feature.dictionary.view.EditTransDialog.argsOf(trans)
                    }
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