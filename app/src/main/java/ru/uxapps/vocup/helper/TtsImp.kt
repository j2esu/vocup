package ru.uxapps.vocup.helper

import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.uxapps.vocup.feature.R
import ru.uxapps.vocup.feature.Tts
import java.util.*

class TtsImp(
    private val activity: FragmentActivity
) : Tts, TextToSpeech.OnInitListener {

    companion object {
        private const val INSTALL_URL = "https://play.google.com/store/apps/details?id=com.google.android.tts"
    }

    private var tts: TextToSpeech? = null
    private var pendingText: String? = null

    private val requestTts =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = TextToSpeech(activity, this)
            } else {
                Toast.makeText(activity, R.string.tts_initializing, Toast.LENGTH_SHORT).show()
                activity.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
            }
        }

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                tts?.shutdown()
            }
        })
    }

    override fun speak(text: String) {
        if (tts != null) {
            speakInner(text)
        } else {
            pendingText = text
            val checkTts = Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
            if (checkTts.resolveActivity(activity.packageManager) != null) {
                requestTts.launch(checkTts)
            } else {
                openInstallPage()
            }
        }
    }

    private fun openInstallPage() {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(INSTALL_URL)))
    }

    private fun speakInner(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.ENGLISH
            pendingText?.let {
                speakInner(it)
                pendingText = null
            }
        } else {
            openInstallPage()
        }
    }
}
