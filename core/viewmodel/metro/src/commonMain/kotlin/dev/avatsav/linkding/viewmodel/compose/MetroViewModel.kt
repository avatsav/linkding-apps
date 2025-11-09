package dev.avatsav.linkding.viewmodel.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.avatsav.linkding.viewmodel.MetroViewModelProviderFactory
import dev.avatsav.linkding.viewmodel.MetroViewModelProviderFactory.Companion.CREATION_CALLBACK_KEY
import dev.avatsav.linkding.viewmodel.ViewModelFactory

@Composable
fun metroViewModelProviderFactory(): MetroViewModelProviderFactory {
  return (LocalViewModelStoreOwner.current as HasDefaultViewModelProviderFactory)
    .defaultViewModelProviderFactory as MetroViewModelProviderFactory
}

@Composable
inline fun <reified VM : ViewModel> metroViewModel(
  viewModelStoreOwner: ViewModelStoreOwner =
    checkNotNull(LocalViewModelStoreOwner.current) {
      "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
  key: String? = null,
): VM {
  return viewModel(viewModelStoreOwner, key, metroViewModelProviderFactory())
}

@Composable
inline fun <reified VM : ViewModel, reified VMF : ViewModelFactory> metroViewModel(
  viewModelStoreOwner: ViewModelStoreOwner =
    checkNotNull(LocalViewModelStoreOwner.current) {
      "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
  key: String? = null,
  noinline creationCallback: (VMF) -> VM,
): VM {
  val extras =
    if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
      viewModelStoreOwner.defaultViewModelCreationExtras.withCreationCallback(creationCallback)
    } else {
      CreationExtras.Empty.withCreationCallback(creationCallback)
    }
  return viewModel(viewModelStoreOwner, key, metroViewModelProviderFactory(), extras)
}

fun <VMF> CreationExtras.withCreationCallback(callback: (VMF) -> ViewModel): CreationExtras =
  MutableCreationExtras(this).addCreationCallback(callback)

@Suppress("UNCHECKED_CAST")
private fun <VMF : ViewModelFactory> MutableCreationExtras.addCreationCallback(
  callback: (VMF) -> ViewModel
): CreationExtras =
  this.apply { this[CREATION_CALLBACK_KEY] = { factory -> callback(factory as VMF) } }
