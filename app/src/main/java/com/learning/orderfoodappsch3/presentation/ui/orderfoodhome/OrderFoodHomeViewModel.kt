package com.learning.orderfoodappsch3.presentation.ui.orderfoodhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.learning.orderfoodappsch3.R
import com.learning.orderfoodappsch3.data.datastore.UserPreferenceDataSource
import com.learning.orderfoodappsch3.data.repository.OrderFoodRepository
import com.learning.orderfoodappsch3.data.repository.UserRepository
import com.learning.orderfoodappsch3.model.Category
import com.learning.orderfoodappsch3.model.OrderFood
import com.learning.orderfoodappsch3.model.User
import com.learning.orderfoodappsch3.utils.AssetWrapper
import com.learning.orderfoodappsch3.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderFoodHomeViewModel(
    val repo: OrderFoodRepository,
    private val userPreferenceDataSource: UserPreferenceDataSource,
    private val userRepo: UserRepository,
    private val assetsWrapper: AssetWrapper
) : ViewModel() {

    private val _orderFoods = MutableLiveData<ResultWrapper<List<OrderFood>>>()
    val orderFoods: LiveData<ResultWrapper<List<OrderFood>>>
        get() = _orderFoods

    fun getOrderFoods(category: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getOrderFoods(if (category == assetsWrapper.getString(R.string.all))null else category?.lowercase())
                .collect {
                    _orderFoods.postValue(it)
                }
        }
    }

    private val _categories = MutableLiveData<ResultWrapper<List<Category>>>()
    val categories: LiveData<ResultWrapper<List<Category>>>
        get() = _categories

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCategories().collect {
                _categories.postValue(it)
            }
        }
    }

    private val _getUserLiveData = MutableLiveData<User?>()
    val getUserLiveData: LiveData<User?>
        get() = _getUserLiveData

    fun getUser() {
        val user = userRepo.getCurrentUser()
        _getUserLiveData.postValue(user)
    }

    fun getUserData() = userRepo.getCurrentUser()

    val layoutMenuListLiveData = userPreferenceDataSource.getListLayoutMenuPrefFlow().asLiveData(Dispatchers.Main)

    fun setListLayoutMenuPref(isLayoutGrid: Boolean) {
        viewModelScope.launch {
            userPreferenceDataSource.setListLayoutMenuPref(isLayoutGrid)
        }
    }
}
