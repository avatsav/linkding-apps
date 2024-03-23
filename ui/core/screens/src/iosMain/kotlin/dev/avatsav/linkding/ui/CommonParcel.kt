package dev.avatsav.linkding.ui

/**
 * https://medium.com/@chrisathanas/how-to-use-parcels-on-kotlin-multiplatform-mobile-kmm-e29590816624
 * https://github.com/realityexpander/NoteAppKMM/
 *
 **/

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
actual annotation class CommonParcelize actual constructor()

actual interface CommonParceler<T>

actual interface CommonParcelable
