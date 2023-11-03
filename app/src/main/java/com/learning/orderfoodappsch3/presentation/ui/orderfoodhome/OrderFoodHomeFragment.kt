package com.learning.orderfoodappsch3.presentation.ui.orderfoodhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.orderfoodappsch3.databinding.FragmentOrderFoodHomeBinding
import com.learning.orderfoodappsch3.model.OrderFood
import com.learning.orderfoodappsch3.presentation.ui.orderfooddetail.DetailOrderFoodActivity
import com.learning.orderfoodappsch3.presentation.ui.orderfoodhome.adapter.AdapterLayoutMode
import com.learning.orderfoodappsch3.presentation.ui.orderfoodhome.adapter.subadapter.CategoriesAdapter
import com.learning.orderfoodappsch3.presentation.ui.orderfoodhome.adapter.subadapter.OrderFoodAdapter
import com.learning.orderfoodappsch3.utils.AssetWrapper
import com.learning.orderfoodappsch3.utils.proceedWhen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderFoodHomeFragment : Fragment() {
    private lateinit var binding: FragmentOrderFoodHomeBinding

    private val viewModel: OrderFoodHomeViewModel by viewModel()
    private val assetWrapper: AssetWrapper by inject()

    private val adapter: OrderFoodAdapter by lazy {
        OrderFoodAdapter(
            modeAdapterLayout = AdapterLayoutMode.LINEAR,
            onListOrderFoodClicked = {
                navigateToDetail(it)
            }
        )
    }

    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter {
            viewModel.getOrderFoods(it.nameCategory.lowercase())
        }
    }

    private fun navigateToDetail(item: OrderFood) {
        DetailOrderFoodActivity.startActivity(requireContext(), item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderFoodHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategories()
        setupList()
        setupToggleLayout()
        switchMode()
        observeData()
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }

    private fun getUserData() {
        viewModel.getUser()
        observeUser()
    }

    private fun observeUser() {
        viewModel.getUserLiveData.observe(viewLifecycleOwner) {
            binding.username.text = viewModel.getUserData()?.fullName
            viewModel.getUser()
        }
    }

    private fun setupCategories() {
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun setupList() {
        val span = if (adapter.modeAdapterLayout == AdapterLayoutMode.LINEAR) 1 else 2
        binding.rvFoodMenu.apply {
            layoutManager = GridLayoutManager(requireContext(), span)
            adapter = this@OrderFoodHomeFragment.adapter
        }
    }

    private fun setupToggleLayout() {
        viewModel.layoutMenuListLiveData.observe(viewLifecycleOwner) { isLayoutGrid ->
            val gridVisibility = if (isLayoutGrid) View.GONE else View.VISIBLE
            val linearVisibility = if (isLayoutGrid) View.VISIBLE else View.GONE

            binding.btnSwitchGrid.visibility = gridVisibility
            binding.btnSwitchList.visibility = linearVisibility

            val spanCount = if (isLayoutGrid) 2 else 1
            (binding.rvFoodMenu.layoutManager as GridLayoutManager).spanCount = spanCount

            adapter.modeAdapterLayout = if (isLayoutGrid) AdapterLayoutMode.GRID else AdapterLayoutMode.LINEAR
            adapter.refreshList()
        }
    }

    private fun switchMode() {
        val switchLayout: (Boolean) -> Unit = { isGrid ->
            viewModel.setListLayoutMenuPref(isGrid)
        }

        binding.btnSwitchGrid.setOnClickListener { switchLayout(true) }
        binding.btnSwitchList.setOnClickListener { switchLayout(false) }
    }

    private fun observeData() {
        viewModel.orderFoods.observe(viewLifecycleOwner) { item ->
            val span =
                if (adapter.modeAdapterLayout == AdapterLayoutMode.LINEAR) 1 else 2
            item.proceedWhen(doOnLoading = {
                binding.layoutStateOrderFood.root.isVisible = true
                binding.layoutStateOrderFood.pbLoading.isVisible = true
                binding.layoutStateOrderFood.tvError.isVisible = false
                binding.rvFoodMenu.isVisible = false
            }, doOnSuccess = {
                    binding.layoutStateOrderFood.root.isVisible = false
                    binding.rvFoodMenu.apply {
                        isVisible = true
                        layoutManager = GridLayoutManager(requireContext(), span)
                        adapter = this@OrderFoodHomeFragment.adapter
                    }
                    binding.layoutStateOrderFood.pbLoading.isVisible = false
                    binding.layoutStateOrderFood.tvError.isVisible = false
                    item.payload?.let {
                            data ->
                        adapter.submitData(data)
                    }
                }, doOnError = {
                    binding.layoutStateOrderFood.root.isVisible = true
                    binding.rvFoodMenu.isVisible = false
                    binding.layoutStateOrderFood.pbLoading.isVisible = false
                    binding.layoutStateOrderFood.tvError.isVisible = true
                })
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = { result ->
                    binding.rvCategories.isVisible = true
                    binding.layoutStateCategory.tvError.isVisible = false
                    binding.layoutStateCategory.pbLoading.isVisible = false

                    result.payload?.let { category ->
                        categoriesAdapter.setItem(category)
                    }
                },
                doOnLoading = {
                    binding.layoutStateCategory.root.isVisible = true
                    binding.layoutStateCategory.pbLoading.isVisible = true
                    binding.rvCategories.isVisible = false
                },
                doOnError = {
                    binding.layoutStateCategory.root.isVisible = true
                    binding.layoutStateCategory.pbLoading.isVisible = false
                    binding.layoutStateCategory.tvError.isVisible = true
                    binding.layoutStateCategory.tvError.text = it.exception?.message.orEmpty()
                    binding.rvCategories.isVisible = false
                }
            )
        }
    }

    private fun fetchData() {
        viewModel.getCategories()
        viewModel.getOrderFoods()
    }
}
