package dev.avatsav.linkding.inject

import org.koin.core.Koin

fun initializeKoin(): Koin = initKoin(Platform.isDebugBinary).koin
