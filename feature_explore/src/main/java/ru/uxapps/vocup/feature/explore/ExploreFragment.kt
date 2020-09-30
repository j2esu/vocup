package ru.uxapps.vocup.feature.explore

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.explore.databinding.FragmentExploreBinding
import ru.uxapps.vocup.feature.explore.model.Explore
import ru.uxapps.vocup.feature.explore.model.ExploreImp
import ru.uxapps.vocup.feature.explore.model.KitItem
import ru.uxapps.vocup.feature.explore.view.ExploreView
import ru.uxapps.vocup.util.consume

class ExploreFragment : BaseFragment(R.layout.fragment_explore) {

    private lateinit var exploreModel: Explore

    override fun onViewReady(view: View, init: Boolean) {
        exploreModel = ExploreImp(lifecycleScope, (activity?.application as RepoProvider).provideRepo())
        val exploreView = ExploreView(FragmentExploreBinding.bind(view), object : ExploreView.Callback {
            override fun onRetry() = exploreModel.onRetry()
            override fun onClick(kit: KitItem, srcView: View) {}
            override fun onSwipe(kit: KitItem) = exploreModel.onDismiss(kit)
        })
        with(exploreModel) {
            kits.observe(viewLifecycleOwner, exploreView::setState)
            onKitDismissed.consume(viewLifecycleOwner) { undo ->
                exploreView.showDismissKitUndo {
                    lifecycleScope.launch { undo() }
                }
            }
        }
    }
}