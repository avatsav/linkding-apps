package dev.avatsav.linkding.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * A [ViewModelProvider.Factory] that uses an injected map of [KClass] to [Provider] of [ViewModel]
 * to create ViewModels.
 */
@ContributesBinding(AppScope::class)
@Inject
@Suppress("MaxLineLength")
class MetroViewModelProviderFactory(private val vmGraphFactory: ViewModelGraph.Factory) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
    val viewModelGraph = createViewModelGraph(extras)
    val provider = viewModelGraph.viewModelProviders[modelClass]
    val assistedFactory = viewModelGraph.viewModelFactoryProviders[modelClass]
    val creationCallback = extras[CREATION_CALLBACK_KEY]

    if (provider != null && assistedFactory != null) {
      error("Found $modelClass in both viewModels and assistedViewModels provider multi-bindings")
    }

    if (provider != null) {
      if (creationCallback != null) {
        error("Found creation callback but $modelClass does not have an assisted factory")
      }
      return modelClass.cast(provider())
    }

    if (assistedFactory != null) {
      if (creationCallback == null) {
        error(
          "Found factory provider for $modelClass using @Assisted but no creation callback was provided in CreationExtras."
        )
      }
      return modelClass.cast(creationCallback.invoke(assistedFactory()))
    }

    error("No provider for $modelClass found in the viewmodel provider multi-binding")
  }

  fun createViewModelGraph(extras: CreationExtras): ViewModelGraph =
    vmGraphFactory.createViewModelGraph(extras)

  companion object Companion {
    internal val CREATION_CALLBACK_KEY =
      object : CreationExtras.Key<(ViewModelFactory) -> ViewModel> {}
  }
}
