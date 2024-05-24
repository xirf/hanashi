package com.andka.penpal.ui.main.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.andka.penpal.databinding.FragmentHomeBinding
import com.andka.penpal.utils.UserPreferences
import com.andka.penpal.utils.datastore
import com.andka.penpal.viewmodels.StoryPaginationViewModel
import com.andka.penpal.viewmodels.UserViewModel
import com.andka.penpal.viewmodels.factory.UserViewModelFactory
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: StoryPaginationViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPreferences: UserPreferences
    private val adapter: StoryAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.rvStory.adapter = adapter

        userPreferences = UserPreferences.getInstance(requireContext().datastore)
        viewModel = ViewModelProvider(this)[StoryPaginationViewModel::class.java]
        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userPreferences)
        )[UserViewModel::class.java]

        getAllStories()

        return binding.root
    }

    private fun getAllStories() {
        lifecycleScope.launch {
            userPreferences.getUserToken().collect { token ->
                viewModel.getAllStories(token, 0)
                viewModel.stories.observe(viewLifecycleOwner) {
                    if (it != null) {
                        adapter.setData(it.listStory)
                        renderRecyclerView()
                    }
                }
            }
        }
    }

    private fun renderRecyclerView() {
        binding.rvStory.layoutManager = GridLayoutManager(context, 2)
        binding.rvStory.addItemDecoration(GridSpacingItemDecoration(2, 16, true))

        adapter.setOnItemClickCallback {
            // val intent = Intent(requireContext(), DetailActivity::class.java)
            // TODO: Add intent.putExtra
        }

    }

}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}