package dev.avatsav.linkding.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.prefs.AppTheme

@Composable
fun PreferenceHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun Preference(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    control: (@Composable () -> Unit)? = null,
) {
    Surface(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            control?.invoke()
        }
    }
}

@Composable
fun SwitchPreference(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Preference(
        title = title,
        description = description,
        control = {
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
            )
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePreference(
    selected: AppTheme,
    onSelected: (AppTheme) -> Unit,
) {
    val options = AppTheme.entries
    Preference(
        title = "Theme",
        description = selected.name,
    ) {
        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, theme ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size,
                    ),
                    onClick = { onSelected(theme) },
                    icon = {},
                    selected = theme == selected,
                ) {
                    Icon(
                        imageVector = when (theme) {
                            AppTheme.System -> Icons.Default.AutoMode
                            AppTheme.Light -> Icons.Default.LightMode
                            AppTheme.Dark -> Icons.Default.DarkMode
                        },
                        contentDescription = theme.name,
                    )
                }
            }
        }
    }
}

