package dev.avatsav.linkding.api

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION,
)
public annotation class LinkdingDsl
