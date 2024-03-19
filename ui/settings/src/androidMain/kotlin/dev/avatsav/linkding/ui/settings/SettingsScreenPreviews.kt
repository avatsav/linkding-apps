package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.ui.theme.LinkdingTheme

@Preview
@Composable
fun SettingsScreenPreview() {
    LinkdingTheme {
        Settings(
            state = SettingsUiState(
                appInfo = AppInfo(
                    packageName = "dev.avatsav.linkding",
                    debug = true,
                    version = "0.1.0-dev",
                ),
                apiConfig = ApiConfig(
                    hostUrl = "https://demo.linkding.link",
                    apiKey = "random-access-rhinoceros",
                ),
                appTheme = AppTheme.System,
                useDynamicColors = true,
            ) {},
        )
    }
}
