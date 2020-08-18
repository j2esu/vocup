package ru.uxapps.vocup.screen.word

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.DialogTransBinding

class EditTransDialog : DialogFragment() {

    interface Host {
        fun onEditTrans(trans: String, newText: String)
        fun onDeleteTrans(trans: String)
    }

    companion object {
        fun argsOf(trans: String) = bundleOf("trans" to trans)
        private val EditTransDialog.trans get() = requireArguments()["trans"] as String
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogTransBinding.inflate(LayoutInflater.from(context))
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .setTitle(R.string.edit_translation)
            .setPositiveButton(R.string.save) { _, _ ->
                val input = binding.transInput.text.toString()
                if (input != trans) {
                    (parentFragment as Host).onEditTrans(trans, input)
                }
            }
            .setNeutralButton(R.string.delete) { _, _ ->
                (parentFragment as Host).onDeleteTrans(trans)
            }
            .create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setOnShowListener {
            val saveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            binding.transInput.apply {
                doAfterTextChanged { saveBtn.isEnabled = !it.isNullOrBlank() }
                setText(trans)
                selectAll()
            }
        }
        return dialog
    }
}