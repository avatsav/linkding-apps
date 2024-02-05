package dev.avatsav.linkding.ui

/**
 * https://medium.com/@chrisathanas/how-to-use-parcels-on-kotlin-multiplatform-mobile-kmm-e29590816624
 * https://github.com/realityexpander/NoteAppKMM/
 *
 **/

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class CommonParcelize()

// For Android @TypeParceler
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
expect annotation class CommonTypeParceler<T, P : CommonParceler<in T>>()

// For Android Parceler
expect interface CommonParceler<T>
