package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordBinding
import ru.uxapps.vocup.util.back
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.target

class WordFragment : Fragment(R.layout.fragment_word), AddTransDialog.Host, EditTransDialog.Host {

    interface Target {
        fun onWordDeleted(word: Word)
    }

    companion object {
        fun argsOf(word: String) = bundleOf("word" to word)
        private val WordFragment.word get() = requireArguments()["word"] as String
    }

    private val vm by viewModels<WordViewModel>()
    private val detailsModel by lazy { vm.wordDetails(word) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val v = WordView(FragmentWordBinding.bind(view), object : WordView.Callback {
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
            onTransDeleted.consume(viewLifecycleOwner, v::showDeleteTransUndo)
            onWordDeleted.consume(viewLifecycleOwner) {
                target<Target>().onWordDeleted(it)
                back()
            }
        }
    }

    override fun onAddTrans(text: String) = detailsModel.onAddTrans(text)
    override fun onEditTrans(trans: String, newText: String) = detailsModel.onEditTrans(trans, newText)
    override fun onDeleteTrans(trans: String) = detailsModel.onDeleteTrans(trans)
}