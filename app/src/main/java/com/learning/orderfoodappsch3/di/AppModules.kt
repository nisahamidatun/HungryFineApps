package com.learning.orderfoodappsch3.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.learning.orderfoodappsch3.data.database.AppDatabase
import com.learning.orderfoodappsch3.data.database.datasource.CartDataSource
import com.learning.orderfoodappsch3.data.database.datasource.CartDataSourceImpl
import com.learning.orderfoodappsch3.data.datastore.UserPreferenceDataSource
import com.learning.orderfoodappsch3.data.datastore.UserPreferenceDataSourceImpl
import com.learning.orderfoodappsch3.data.datastore.appDataStore
import com.learning.orderfoodappsch3.data.network.api.datasource.RestaurantApiDataSource
import com.learning.orderfoodappsch3.data.network.api.datasource.RestaurantDataSource
import com.learning.orderfoodappsch3.data.network.api.service.RestaurantService
import com.learning.orderfoodappsch3.data.network.firebase.auth.FirebaseAuthDataSource
import com.learning.orderfoodappsch3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.learning.orderfoodappsch3.data.repository.CartRepo
import com.learning.orderfoodappsch3.data.repository.CartRepoImpl
import com.learning.orderfoodappsch3.data.repository.OrderFoodRepository
import com.learning.orderfoodappsch3.data.repository.OrderFoodRepositoryImpl
import com.learning.orderfoodappsch3.data.repository.UserRepository
import com.learning.orderfoodappsch3.data.repository.UserRepositoryImpl
import com.learning.orderfoodappsch3.presentation.ui.cart.CartViewModel
import com.learning.orderfoodappsch3.presentation.ui.checkout.CheckoutViewModel
import com.learning.orderfoodappsch3.presentation.ui.login.LoginViewModel
import com.learning.orderfoodappsch3.presentation.ui.orderfooddetail.DetailOrderFoodViewModel
import com.learning.orderfoodappsch3.presentation.ui.orderfoodhome.OrderFoodHomeViewModel
import com.learning.orderfoodappsch3.presentation.ui.profile.ProfileChangeViewModel
import com.learning.orderfoodappsch3.presentation.ui.profile.ProfileViewModel
import com.learning.orderfoodappsch3.presentation.ui.register.RegisterViewModel
import com.learning.orderfoodappsch3.presentation.ui.splashscreen.SplashViewModel
import com.learning.orderfoodappsch3.utils.AssetWrapper
import com.learning.orderfoodappsch3.utils.PreferenceDataStoreHelper
import com.learning.orderfoodappsch3.utils.PreferenceDataStoreHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {
    private val localModule = module {
        single { AppDatabase.getInstance(androidContext()) }
        single { get<AppDatabase>().cartDao() }
        single { androidContext().appDataStore }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
    }

    private val networkModule = module {
        single { ChuckerInterceptor(androidContext()) }
        single { RestaurantService.invoke(get()) }
        single { FirebaseAuth.getInstance() }
    }

    private val dataSourceModule = module {
        single<CartDataSource> { CartDataSourceImpl(get()) }
        single<UserPreferenceDataSource> { UserPreferenceDataSourceImpl(get()) }
        single<RestaurantDataSource> { RestaurantApiDataSource(get()) }
        single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get()) }
    }

    private val repositoryModule = module {
        single<CartRepo> { CartRepoImpl(get(), get()) }
        single<OrderFoodRepository> { OrderFoodRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::OrderFoodHomeViewModel)
        viewModelOf(::CartViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::RegisterViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::ProfileChangeViewModel)
        viewModelOf(::CheckoutViewModel)
        viewModelOf(::DetailOrderFoodViewModel)
    }

    private val utilsModule = module {
        single { AssetWrapper(androidContext()) }
    }

    val modules: List<Module> = listOf(
        localModule,
        networkModule,
        dataSourceModule,
        repositoryModule,
        viewModelModule,
        utilsModule
    )
}
