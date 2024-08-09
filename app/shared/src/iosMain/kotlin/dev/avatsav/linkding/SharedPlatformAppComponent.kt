package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual interface SharedPlatformAppComponent {

    @AppScope
    @Provides
    fun provideAppInfo(): AppInfo = AppInfo(
        packageName = NSBundle.mainBundle.bundleIdentifier ?: error("Bundle ID not found"),
        debug = Platform.isDebugBinary,
        version = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: "",
    )

    @AppScope
    @Provides
    fun provideNsUserDefaults(): NSUserDefaults = NSUserDefaults.standardUserDefaults
}
