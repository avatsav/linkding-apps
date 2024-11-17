package dev.avatsav.linkding

import dev.avatsav.linkding.initializers.AppInitializer
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.experimental.ExperimentalNativeApi

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
@OptIn(ExperimentalNativeApi::class)
abstract class IosAppComponent {

    abstract val appInitializer: AppInitializer

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
}

@MergeComponent.CreateComponent
expect fun createIosAppComponent(): IosAppComponent
