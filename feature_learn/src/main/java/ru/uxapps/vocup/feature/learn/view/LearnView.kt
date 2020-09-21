package ru.uxapps.vocup.feature.learn.view

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.feature.learn.databinding.FragmentLearnBinding
import ru.uxapps.vocup.feature.learn.model.GameItem

internal class LearnView(
    private val bind: FragmentLearnBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onStart(item: GameItem)
        fun onPlay()
    }

    private val exAdapter = GameListAdapter { callback.onStart(it) }

    init {
        bind.learnRv.apply {
            adapter = exAdapter
            layoutManager = LinearLayoutManager(context)
        }
        bind.learnPlay.setOnClickListener { callback.onPlay() }
    }

    fun setGames(games: List<GameItem>) {
        exAdapter.submitList(games)
    }

    fun setPlayEnabled(enabled: Boolean) {
        bind.learnPlay.isVisible = enabled
    }
}