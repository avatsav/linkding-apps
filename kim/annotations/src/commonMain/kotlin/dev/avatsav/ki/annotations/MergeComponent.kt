package dev.avatsav.ki.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@Retention(RUNTIME)
annotation class MergeComponent(
    val scope: KClass<*>,
)
