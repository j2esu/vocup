package ru.uxapps.vocup.feature.explore

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Kit
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitValue
import ru.uxapps.vocup.feature.explore.databinding.FragmentExploreBinding
import ru.uxapps.vocup.feature.explore.di.ExploreViewModel
import ru.uxapps.vocup.feature.explore.model.Explore
import ru.uxapps.vocup.feature.explore.model.KitItem
import ru.uxapps.vocup.feature.explore.view.ExploreView
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class ExploreFragment : BaseFragment(R.layout.fragment_explore) {

    interface Router {
        fun openAddKit(kit: Kit, srcView: View)
    }

    private val vm by viewModels<ExploreViewModel>()

    @Inject internal lateinit var exploreModel: Explore

    override fun onViewReady(view: View, init: Boolean) {
        vm.exploreComponent.inject(this)
        val exploreView = ExploreView(FragmentExploreBinding.bind(view), object : ExploreView.Callback {
            override fun onRetry() = exploreModel.onRetry()
            override fun onClick(item: KitItem, srcView: View) = host<Router>().openAddKit(item.kit, srcView)
            override fun onSwipe(item: KitItem) = exploreModel.onDismiss(item)
        })
        with(exploreModel) {
            kits.observe(viewLifecycleOwner, exploreView::setState)
            onKitDismissed.consume(viewLifecycleOwner) { undo ->
                exploreView.showDismissKitUndo {
                    lifecycleScope.launch { undo() }
                }
            }
        }
        postponeUntil {
            exploreModel.kits.awaitValue()
        }
    }
}