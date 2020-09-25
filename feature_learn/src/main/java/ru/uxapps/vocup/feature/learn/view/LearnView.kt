package ru.uxapps.vocup.feature.learn.view

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.feature.learn.databinding.FragmentLearnBinding
import ru.uxapps.vocup.feature.learn.model.GameItem

internal class LearnView(
    bind: FragmentLearnBinding,
    callback: Callback
) {

    interface Callback {
        fun onStart(item: GameItem, srcView: View)
    }

    private val gameAdapter = GameListAdapter(callback::onStart)

    init {
        bind.learnRv.apply {
            adapter = gameAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun setGames(games: List<GameItem>) = gameAdapter.submitList(games)
}