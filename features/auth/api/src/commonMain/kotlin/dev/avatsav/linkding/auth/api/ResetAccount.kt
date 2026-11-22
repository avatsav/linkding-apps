package dev.avatsav.linkding.auth.api

/** Clears the current credentials unless there are unsent drafts or the account has changed. */
fun interface ResetAccount {
  suspend operator fun invoke(): Boolean
}
