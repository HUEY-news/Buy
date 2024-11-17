package com.houston.buy.domain.api

import android.net.Uri
import com.houston.buy.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductInteractor {
    fun getProductList(): Flow<List<Product>>
    suspend fun createNewProduct(name: String, description: String, image: Uri?)
    suspend fun removeProduct(id: Int)
}
