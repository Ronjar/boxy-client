package com.robingebert.boxy.main


import com.robingebert.boxy.data.DataStoreManager
import com.robingebert.boxy.data.network.BoxyKtorClient
import com.robingebert.boxy.data.network.StorageApi
import com.robingebert.boxy.domain.AssetRepository
import com.robingebert.boxy.domain.LocationRepository
import com.robingebert.boxy.domain.SyncRepository
import com.robingebert.boxy.ui.main.MainViewModel
import com.robingebert.boxy.ui.sync.SyncViewModel
import com.robingebert.boxy.ui.overview.OverviewViewModel
import com.robingebert.boxy.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


object AppModule {
    fun modules() = commonModule + viewModelModule
}

val viewModelModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::OverviewViewModel)
    viewModelOf(::SyncViewModel)
    viewModelOf(::MainViewModel)
}

val commonModule = module {
    single { DataStoreManager(androidContext()) }
    single {
        val dataStore = get<DataStoreManager>()

        BoxyKtorClient(
            getUrl = { dataStore.url.flow.value },
            getUsername = { dataStore.username.flow.value },
            getPassword = { dataStore.password.flow.value }
        ).client
    }
    single { StorageApi(get()) }

    // Repositories
    single { AssetRepository(get()) }
    single { LocationRepository(get()) }
    single { SyncRepository(get(), get()) }
}