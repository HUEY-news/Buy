package com.houston.buy.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houston.buy.domain.api.ProductInteractor
import com.houston.buy.domain.model.ProductDatabaseScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDatabaseViewModel @Inject constructor(
    private val interactor: ProductInteractor
) : ViewModel() {

    private val currentScreenState = MutableLiveData<ProductDatabaseScreenState>()
    fun observeScreenState(): LiveData<ProductDatabaseScreenState> = currentScreenState
    private fun renderState(state: ProductDatabaseScreenState) { currentScreenState.postValue(state) }

    init { updateScreenState() }
    fun onResume() { updateScreenState() }

    private fun updateScreenState() {
        viewModelScope.launch {
            interactor
                .getProductList()
                .collect { list ->
                    if (list.isEmpty()) renderState(ProductDatabaseScreenState.Empty)
                    else renderState(ProductDatabaseScreenState.Content(list))
                }
        }
    }

    fun removeProduct(id: Int) {
        viewModelScope.launch {
            interactor.removeProduct(id)
            updateScreenState()
        }
    }
}
