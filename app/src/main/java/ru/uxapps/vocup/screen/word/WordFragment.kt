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
import ru.uxapps.vocup.nav
import ru.uxapps.vocup.util.consume

class WordFragment : Fragment(R.layout.fragment_word), AddTransDialog.Host, EditTransDialog.Host {

    interface Host {
        fun onDeleteWord(text: String)
    }

    companion object {
        fun argsOf(word: Word) = bundleOf("word" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    private val vm by viewModels<WordViewModel>()
    private val details by lazy { vm.wordDetails(wordText) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val v = WordView(FragmentWordBinding.bind(view), object : WordView.Callback {
            override fun onUp() = nav.up()
            override fun onDelete() = (activity as Host).onDeleteWord(wordText)
            override fun onAddTrans() = AddTransDialog().show(childFragmentManager, null)
            override fun onDeleteTrans(trans: String) = details.onDeleteTrans(trans)
            override fun onListen() {
                Toast.makeText(
                    context, "You're listening ${details.text.value}", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onReorderTrans(newTrans: List<String>) = details.onReorderTrans(newTrans)
            override fun onEditTrans(trans: String) {
                EditTransDialog().apply { arguments = EditTransDialog.argsOf(trans) }
                    .show(childFragmentManager, null)
            }
        })
        with(details) {
            translations.observe(viewLifecycleOwner, v::setTranslations)
            text.observe(viewLifecycleOwner, v::setWordText)
            pron.observe(viewLifecycleOwner, v::setPronText)
            onTransDeleted.consume(viewLifecycleOwner, v::showDeleteTransUndo)
        }
    }

    override fun onAddTrans(text: String) = details.onAddTrans(text)
    override fun onEditTrans(trans: String, newText: String) = details.onEditTrans(trans, newText)
    override fun onDeleteTrans(trans: String) = details.onDeleteTrans(trans)
}