package ru.uxapps.vocup.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.uxapps.vocup.feature.R
import ru.uxapps.vocup.feature.Tts
import java.util.*

class TtsImp(
    lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val callback: Callback
) : Tts, TextToSpeech.OnInitListener {

    interface Callback {
        fun onStartActivity(intent: Intent, requestCode: Int? = null)
    }

    companion object {
        private const val REQUEST_TTS = 10
        private const val INSTALL_URL = "https://play.google.com/store/apps/details?id=com.google.android.tts"
    }

    private var mTts: TextToSpeech? = null
    private var pendingText: String? = null

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                mTts?.shutdown()
            }
        })
    }

    override fun speak(text: String) {
        if (mTts != null) {
            speakInner(text)
        } else {
            pendingText = text
            val checkTts = Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
            if (checkTts.resolveActivity(context.packageManager) != null) {
                callback.onStartActivity(checkTts, REQUEST_TTS)
            } else {
                openInstallPage()
            }
        }
    }

    private fun openInstallPage() {
        callback.onStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(INSTALL_URL)))
    }

    private fun speakInner(text: String) {
        mTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onActivityResult(resultCode: Int) {
        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            mTts = TextToSpeech(context, this)
            mTts?.language = Locale.ENGLISH
        } else {
            Toast.makeText(context, R.string.tts_initializing, Toast.LENGTH_SHORT).show()
            callback.onStartActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            pendingText?.let {
                speakInner(it)
                pendingText = null
            }
        } else {
            openInstallPage()
        }
    }
}