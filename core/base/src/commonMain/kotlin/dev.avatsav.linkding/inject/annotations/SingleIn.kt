package dev.avatsav.linkding.inject.annotations

import me.tatarka.inject.annotations.Scope
import kotlin.reflect.KClass

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleIn(val scope: KClass<*>)
