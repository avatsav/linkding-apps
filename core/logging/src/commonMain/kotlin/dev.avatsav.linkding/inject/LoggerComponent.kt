package dev.avatsav.linkding.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.KermitLogger
import dev.avatsav.linkding.Logger
import me.tatarka.inject.annotations.Provides

interface LoggerComponent {

    @AppScope
    @Provides
    fun provideLogger(appInfo: AppInfo): Logger = KermitLogger(appInfo)
}
