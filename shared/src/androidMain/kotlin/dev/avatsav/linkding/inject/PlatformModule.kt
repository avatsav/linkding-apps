package dev.avatsav.linkding.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import dev.avatsav.linkding.Constants
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


@OptIn(ExperimentalSettingsApi::class)
actual fun platformModule() = module {
    single { flowSettings(androidApplication()) }
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(Constants.SettingsKey)

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
private fun flowSettings(context: Context): FlowSettings {
    return DataStoreSettings(context.settingsDataStore)
}
