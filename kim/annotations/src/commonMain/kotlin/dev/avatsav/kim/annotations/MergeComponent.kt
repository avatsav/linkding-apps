package dev.avatsav.kim.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class MergeComponent(
    val scope: KClass<*>,
)
