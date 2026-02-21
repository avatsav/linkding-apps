package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import linkding_apps.ui.compose.generated.resources.Res
import linkding_apps.ui.compose.generated.resources.remove_tag
import org.jetbrains.compose.resources.stringResource

@Composable
fun TagInputChip(
  label: @Composable () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  InputChip(
    modifier = modifier,
    selected = false,
    onClick = { /*NO OP*/ },
    label = label,
    trailingIcon = {
      IconButton(modifier = Modifier.size(12.dp), onClick = { onClick() }) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = stringResource(Res.string.remove_tag),
        )
      }
    },
  )
}
