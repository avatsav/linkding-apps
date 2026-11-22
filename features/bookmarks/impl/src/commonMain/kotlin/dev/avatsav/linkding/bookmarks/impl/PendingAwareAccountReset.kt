package dev.avatsav.linkding.bookmarks.impl

import dev.avatsav.linkding.auth.api.ResetAccount
import dev.avatsav.linkding.data.db.daos.PendingBookmarksDao
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(UserScope::class)
@ContributesBinding(UserScope::class)
class PendingAwareAccountReset(
  private val dao: PendingBookmarksDao,
  private val preferences: AppPreferences,
  private val config: ApiConfig,
  private val coordinator: PendingBookmarksCoordinator,
) : ResetAccount {
  private val accountId = preferences.getAccountId()

  override suspend fun invoke(): Boolean =
    coordinator.locally(accountId) {
      // Hold the same lock used by create; check the account again after the database read.
      if (dao.getAll(accountId).isNotEmpty() || !isCurrentAccount) return@locally false
      preferences.setApiConfig(null)
      true
    }

  private val isCurrentAccount: Boolean
    get() = preferences.getApiConfig() == config && preferences.getAccountId() == accountId
}
