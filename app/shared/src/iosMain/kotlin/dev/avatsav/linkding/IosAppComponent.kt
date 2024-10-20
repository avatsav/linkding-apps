package dev.avatsav.linkding

import com.r0adkll.kimchi.annotations.MergeComponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
import kotlin.experimental.ExperimentalNativeApi

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
@OptIn(ExperimentalNativeApi::class)
abstract class IosAppComponent {

    @SingleIn(AppScope::class)
    @Provides
    fun provideAppInfo(): AppInfo = AppInfo(
        packageName = NSBundle.mainBundle.bundleIdentifier ?: error("Bundle ID not found"),
        debug = Platform.isDebugBinary,
        version = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: "",
    )

    @SingleIn(AppScope::class)
    @Provides
    fun provideNsUserDefaults(): NSUserDefaults = NSUserDefaults.standardUserDefaults

    companion object
}
