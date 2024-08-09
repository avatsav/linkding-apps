package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagInputChip(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    InputChip(
        modifier = modifier,
        selected = false,
        onClick = { /*NO OP*/ },
        label = label,
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        trailingIcon = {
            IconButton(
                modifier = Modifier.size(12.dp),
                onClick = { onClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove tag",
                )
            }
        },
    )
}
