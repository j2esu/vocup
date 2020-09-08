package ru.uxapps.vocup.feature.worddetails

import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.feature.delayTransition
import ru.uxapps.vocup.feature.worddetails.databinding.FragmentWordBinding
import ru.uxapps.vocup.feature.worddetails.di.WordViewModel
import ru.uxapps.vocup.feature.worddetails.model.WordDetails
import ru.uxapps.vocup.feature.worddetails.view.AddTransDialog
import ru.uxapps.vocup.feature.worddetails.view.EditTransDialog
import ru.uxapps.vocup.feature.worddetails.view.WordView
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class WordFragment : Fragment(R.layout.fragment_word), AddTransDialog.Host, EditTransDialog.Host {

    interface Router {
        fun onWordDeleted(undo: suspend () -> Unit)
    }

    companion object {
        fun argsOf(word: String) = bundleOf("word" to word)
        private val WordFragment.word get() = requireArguments()["word"] as String
    }

    private val vm by viewModels<WordViewModel>()

    @Inject internal lateinit var detailsModel: WordDetails

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.getWordComponent(word).inject(this)
        val v = WordView(FragmentWordBinding.bind(requireView()), object : WordView.Callback {

            override fun onDelete() = detailsModel.onDeleteWord()
            override fun onAddTrans() = AddTransDialog().show(childFragmentManager, null)
            override fun onDeleteTrans(trans: String) = detailsModel.onDeleteTrans(trans)
            override fun onReorderTrans(newTrans: List<String>) = detailsModel.onReorderTrans(newTrans)

            override fun onListen() {
                Toast.makeText(context, "You're listening ${detailsModel.text.value}", Toast.LENGTH_SHORT)
                    .show()
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
        delayTransition(detailsModel.translations, detailsModel.text, detailsModel.pron)
    }

    override fun onAddTrans(text: String) = detailsModel.onAddTrans(text)
    override fun onEditTrans(trans: String, newText: String) = detailsModel.onEditTrans(trans, newText)
    override fun onDeleteTrans(trans: String) = detailsModel.onDeleteTrans(trans)
}