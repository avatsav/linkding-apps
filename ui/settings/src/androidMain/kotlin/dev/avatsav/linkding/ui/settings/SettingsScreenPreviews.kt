package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.ui.theme.LinkdingTheme

@Preview
@Composable
fun SettingsScreenPreview() {
    LinkdingTheme {
        Settings(
            state = SettingsUiState(
                null,
                AppTheme.System,
                true,
            ) {},
        )
    }
}
