package com.houston.buy.ui.productDatabase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.houston.buy.R
import com.houston.buy.databinding.FragmentProductDatabaseBinding
import com.houston.buy.domain.model.Product
import com.houston.buy.domain.model.ProductDatabaseScreenState
import com.houston.buy.presentation.ProductDatabaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDatabaseFragment : Fragment() {
    private var _binding: FragmentProductDatabaseBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<ProductDatabaseViewModel>()

    private var adapter: ProductDatabaseAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDatabaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewAdapter()
        setupButtonListeners()

        viewModel.observeScreenState().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ProductDatabaseScreenState.Content -> showContent(screenState.data)
                is ProductDatabaseScreenState.Empty -> showEmpty()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun setupRecyclerViewAdapter() {
        adapter = ProductDatabaseAdapter { product: Product -> onClickDebounce(product) }
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupButtonListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.action_databaseFragment_to_addingFragment)
        }
    }

    private fun showContent(data: List<Product>) {
        showEmptyPlaceholder(false)
        updateProductList(data)
        showProductList(true)
    }

    private fun showEmpty() {
        showEmptyPlaceholder(true)
        updateProductList(listOf())
        showProductList(false)
    }

    private fun updateProductList(data: List<Product>) {
        adapter?.setItems(data)
    }

    private fun showProductList(isVisible: Boolean) {
        binding.recycler.isVisible = isVisible
    }

    private fun showEmptyPlaceholder(isVisible: Boolean) {
        binding.placeholderContainer.isVisible = isVisible
    }

    private fun onClickDebounce(product: Product) {
        if (clickDebounce()) {
            viewModel.removeProduct(product.id)
        }
    }

    private var isClickAllowed = true

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
