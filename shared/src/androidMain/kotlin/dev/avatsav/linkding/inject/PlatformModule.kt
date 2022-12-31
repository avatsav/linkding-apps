package dev.avatsav.linkding.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import dev.avatsav.linkding.Constants
import dev.avatsav.linkding.LinkUnfurler
import me.saket.unfurl.Unfurler
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


actual fun platformModule() = module {
    single { flowSettings(androidApplication()) }
    single { LinkUnfurler(Unfurler()) }
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(Constants.SettingsKey)

private fun flowSettings(context: Context): FlowSettings {
    return DataStoreSettings(context.settingsDataStore)
}
