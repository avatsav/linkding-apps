package dev.avatsav.kim.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@Retention(RUNTIME)
annotation class ContributesTo(
    val scope: KClass<*>,
)
