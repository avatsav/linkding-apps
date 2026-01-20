package dev.avatsav.gradle

internal object CompilerOptions {

  val freeCompilerArgs =
    listOf(
      "-Xskip-prerelease-check",
      "-Xexpect-actual-classes",
      "-Xreturn-value-checker=full",
      "-Xexplicit-backing-fields",
      "-Xdata-flow-based-exhaustiveness",
      "-Xcontext-sensitive-resolution",
    )

  val optIn =
    listOf("kotlin.uuid.ExperimentalUuidApi", "kotlinx.coroutines.ExperimentalCoroutinesApi")

  val nativeOptionIn =
    listOf("kotlinx.cinterop.ExperimentalForeignApi", "kotlinx.cinterop.BetaInteropApi")
}
