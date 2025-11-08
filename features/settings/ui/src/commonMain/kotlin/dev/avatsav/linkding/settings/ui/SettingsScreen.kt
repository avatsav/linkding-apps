package dev.avatsav.linkding.settings.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.DialogResult
import com.slack.circuitx.overlays.alertDialogOverlay
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.Close
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ResetApiConfig
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.SetAppTheme
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowLicenses
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowPrivacyPolicy
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowSourceCode
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ToggleUseDynamicColors
import dev.avatsav.linkding.settings.ui.widgets.Preference
import dev.avatsav.linkding.settings.ui.widgets.PreferenceSection
import dev.avatsav.linkding.settings.ui.widgets.SwitchPreference
import dev.avatsav.linkding.settings.ui.widgets.ThemePreference
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.theme.Material3ShapeDefaults
import kotlinx.coroutines.launch

@CircuitInject(SettingsScreen::class, UserScope::class)
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Settings(state: SettingsUiState, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink
  val overlayHost = LocalOverlayHost.current

  val coroutineScope = rememberCoroutineScope()
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeFlexibleTopAppBar(
        title = { Text(text = "Settings") },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
          IconButton(onClick = { eventSink(Close) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Default.ArrowBack,
              contentDescription = "Navigate Back",
            )
          }
        },
      )
    },
  ) { paddingValues ->
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            top = paddingValues.calculateTopPadding(),
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
          )
          .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
      item {
        LinkdingSettings(
          state = state,
          onResetClick = {
            coroutineScope.launch {
              val result = overlayHost.showResetConfirmationDialog()
              if (result == DialogResult.Confirm) eventSink(ResetApiConfig)
            }
          },
        )
      }
      item {
        AppearanceSettings(
          state = state,
          onAppThemeChange = { eventSink(SetAppTheme(it)) },
          onDynamicThemeToggle = { eventSink(ToggleUseDynamicColors) },
        )
      }
      item {
        AboutSettings(
          state = state,
          onSourceCodeClick = { eventSink(ShowSourceCode) },
          onLicensesClick = { eventSink(ShowLicenses) },
          onPrivacyPolicyClick = { eventSink(ShowPrivacyPolicy) },
        )
      }
      item { MadeInMunich() }
    }
  }
}

private const val LinkdingSettingsCount = 3

@Composable
@Suppress("MagicNumber")
private fun LinkdingSettings(state: SettingsUiState, onResetClick: () -> Unit) {
  PreferenceSection("Linkding", modifier = Modifier.padding(vertical = 8.dp)) {
    Preference(
      shape = Material3ShapeDefaults.itemShape(0, LinkdingSettingsCount),
      title = "Host Url",
      description = state.apiConfig?.hostUrl,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(1, LinkdingSettingsCount),
      title = "API Key",
      description = state.apiConfig?.apiKey,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(2, LinkdingSettingsCount),
      title = "Reset",
      clickable = true,
      onClick = onResetClick,
    )
  }
}

private const val AppearanceSettingsCount = 2

@Composable
@Suppress("MagicNumber")
private fun AppearanceSettings(
  state: SettingsUiState,
  onAppThemeChange: (AppTheme) -> Unit,
  onDynamicThemeToggle: () -> Unit,
) {

  PreferenceSection("Appearance", modifier = Modifier.padding(vertical = 8.dp)) {
    ThemePreference(
      shape = Material3ShapeDefaults.itemShape(0, AppearanceSettingsCount),
      selected = state.appTheme,
      onSelect = onAppThemeChange,
    )
    SwitchPreference(
      shape = Material3ShapeDefaults.itemShape(1, AppearanceSettingsCount),
      title = "Dynamic colours",
      description = "Colors adapt to your wallpaper",
      checked = state.useDynamicColors,
      onCheckedChange = onDynamicThemeToggle,
    )
  }
}

private const val AboutSettingsCount = 4

@Composable
@Suppress("MagicNumber")
private fun AboutSettings(
  state: SettingsUiState,
  onSourceCodeClick: () -> Unit,
  onLicensesClick: () -> Unit,
  onPrivacyPolicyClick: () -> Unit,
) {
  PreferenceSection("About", modifier = Modifier.padding(vertical = 8.dp)) {
    Preference(
      shape = Material3ShapeDefaults.itemShape(0, AboutSettingsCount),
      title = "Version",
      description = state.appInfo.version,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(1, AboutSettingsCount),
      title = "Source code",
      clickable = true,
      onClick = onSourceCodeClick,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(2, AboutSettingsCount),
      title = "Open Source licenses",
      clickable = true,
      onClick = onLicensesClick,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(3, AboutSettingsCount),
      title = "Privacy policy",
      clickable = true,
      onClick = onPrivacyPolicyClick,
    )
  }
}

@Composable
private fun MadeInMunich() {
  Text(
    text = "Made with \uD83E\uDD68 in München",
    style = MaterialTheme.typography.bodyMedium,
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).navigationBarsPadding(),
    textAlign = TextAlign.Center,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun OverlayHost.showResetConfirmationDialog(): DialogResult =
  show(
    alertDialogOverlay(
      icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Logout, "") },
      title = { Text("Confirm Reset") },
      text = { Text("Are you sure you want to reset the api configuration?") },
      confirmButton = { onClick -> Button(onClick = onClick) { Text("Yes") } },
      dismissButton = { onClick -> OutlinedButton(onClick = onClick) { Text("No") } },
    )
  )
