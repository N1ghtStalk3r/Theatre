package com.example.theatre.features.spectacles.presentation.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.theatre.R
import com.example.theatre.core.domain.model.common.performance.Performance
import com.example.theatre.core.presentation.model.ContentResultState
import com.example.theatre.core.presentation.model.handleContents
import com.example.theatre.core.presentation.model.refreshPage
import com.example.theatre.core.presentation.ui.LayoutErrorHandler
import com.example.theatre.databinding.FragmentSpectaclesBinding
import com.example.theatre.features.poster.domain.model.PosterBriefItem
import com.example.theatre.features.spectacles.presentation.adapters.EventListAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Фрагмент со списком постановок
 *
 * @author Tamerlan Mamukhov
 */

class SpectaclesListFragment : Fragment() {

    private lateinit var binding: FragmentSpectaclesBinding
    private lateinit var performancesAdapter: EventListAdapter
    private val spectaclesListViewModel by viewModel<SpectaclesListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spectacles, container, false)

        performancesAdapter = EventListAdapter(mutableListOf()) { id ->
            onSpectacleClick(id)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSpectaclesBinding.bind(view)

        initObservers()
        spectaclesListViewModel.init()
    }

    private fun initObservers() {
        spectaclesListViewModel.spectacleLoaded.observe(viewLifecycleOwner, ::handleSpectacles)
    }

    private fun handleSpectacles(contentResultState: ContentResultState) =
        contentResultState.refreshPage(
            onStateSuccess = {
                performancesAdapter.setSpectacles(it as List<Performance>)
                binding.listEvent.adapter = performancesAdapter
            },
            tryAgainAction = {
                tryAgain()
            },
            viewToShow = binding.content,
            progressBar = binding.progressBar3,
            errorLayout = binding.errorLayout
        )

    private fun tryAgain() {
        spectaclesListViewModel.init()
    }

    private fun onSpectacleClick(id: Int) {
        val bundle = bundleOf("id" to id)
        requireActivity().findNavController(R.id.navHostFragment)
            .navigate(R.id.action_performance_to_eventFragment, bundle)
    }
}
