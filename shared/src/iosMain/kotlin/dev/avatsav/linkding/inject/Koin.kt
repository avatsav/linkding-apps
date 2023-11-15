package dev.avatsav.linkding.inject

import kotlin.experimental.ExperimentalNativeApi
import org.koin.core.Koin

@OptIn(ExperimentalNativeApi::class)
fun initializeKoin(): Koin = initKoin(Platform.isDebugBinary).koin
