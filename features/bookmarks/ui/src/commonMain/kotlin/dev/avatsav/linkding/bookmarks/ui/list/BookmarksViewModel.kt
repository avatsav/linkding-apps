package dev.avatsav.linkding.bookmarks.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ContributesIntoMap(UserScope::class, binding<ViewModel>())
@ViewModelKey(BookmarksViewModel::class)
@Inject
class BookmarksViewModel(presenterFactory: BookmarksPresenter.Factory) :
  MoleculeViewModel<BookmarksUiEvent, BookmarksUiState, BookmarkUiEffect>() {
  override val presenter by lazy { presenterFactory.create(viewModelScope) }
}
