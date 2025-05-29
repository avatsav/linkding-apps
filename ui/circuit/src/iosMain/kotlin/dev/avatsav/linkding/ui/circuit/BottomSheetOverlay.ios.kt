// Copyright (C) 2024 Slack Technologies, LLC
// SPDX-License-Identifier: Apache-2.0
package dev.avatsav.linkding.ui.circuit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties

@OptIn(ExperimentalMaterial3Api::class)
internal actual fun createBottomSheetProperties(
  shouldDismissOnBackPress: Boolean
): ModalBottomSheetProperties {
  return ModalBottomSheetProperties(shouldDismissOnBackPress)
}
