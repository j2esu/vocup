package ru.uxapps.vocup.screen.word

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.DialogTransBinding

class AddTransDialog : DialogFragment() {

    interface Host {
        fun onAddTrans(text: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogTransBinding.inflate(LayoutInflater.from(context))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.add_translation)
            .setPositiveButton(R.string.add) { _, _ ->
                (parentFragment as Host).onAddTrans(binding.transInput.text.toString())
            }
            .create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setOnShowListener {
            val addBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            binding.transInput.apply {
                doAfterTextChanged { addBtn.isEnabled = !it.isNullOrBlank() }
                text = text // trigger initial validation
            }
        }
        return dialog
    }
}