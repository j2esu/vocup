package ru.uxapps.vocup.util

import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

fun EditText.input(scope: CoroutineScope = GlobalScope): ReceiveChannel<String> {
    val channel = Channel<String>(Channel.CONFLATED)
    scope.launch {
        channel.send(text.toString())
    }
    doAfterTextChanged {
        scope.launch {
            channel.send(it.toString())
        }
    }
    return channel
}

fun View.clicks(scope: CoroutineScope = GlobalScope): ReceiveChannel<Unit> {
    val channel = Channel<Unit>(Channel.UNLIMITED)
    setOnClickListener {
        scope.launch {
            channel.send(Unit)
        }
    }
    return channel
}