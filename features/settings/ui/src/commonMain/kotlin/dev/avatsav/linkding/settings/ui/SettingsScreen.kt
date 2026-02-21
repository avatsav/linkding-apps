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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.presenter.ObserveEffects
import dev.avatsav.linkding.settings.ui.SettingsUiEffect.NavigateUp
import dev.avatsav.linkding.settings.ui.SettingsUiEffect.OpenUrl
import dev.avatsav.linkding.settings.ui.SettingsUiEffect.ResetToAuth
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
import dev.avatsav.linkding.ui.theme.Material3ShapeDefaults
import linkding_apps.features.settings.ui.generated.resources.Res
import linkding_apps.features.settings.ui.generated.resources.settings_about
import linkding_apps.features.settings.ui.generated.resources.settings_api_key
import linkding_apps.features.settings.ui.generated.resources.settings_appearance
import linkding_apps.features.settings.ui.generated.resources.settings_confirm_reset_message
import linkding_apps.features.settings.ui.generated.resources.settings_confirm_reset_title
import linkding_apps.features.settings.ui.generated.resources.settings_dynamic_colours
import linkding_apps.features.settings.ui.generated.resources.settings_dynamic_colours_description
import linkding_apps.features.settings.ui.generated.resources.settings_host_url
import linkding_apps.features.settings.ui.generated.resources.settings_licenses
import linkding_apps.features.settings.ui.generated.resources.settings_linkding
import linkding_apps.features.settings.ui.generated.resources.settings_made_in_munich
import linkding_apps.features.settings.ui.generated.resources.settings_navigate_back
import linkding_apps.features.settings.ui.generated.resources.settings_no
import linkding_apps.features.settings.ui.generated.resources.settings_privacy_policy
import linkding_apps.features.settings.ui.generated.resources.settings_reset
import linkding_apps.features.settings.ui.generated.resources.settings_source_code
import linkding_apps.features.settings.ui.generated.resources.settings_title
import linkding_apps.features.settings.ui.generated.resources.settings_version
import linkding_apps.features.settings.ui.generated.resources.settings_yes
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(presenter: SettingsPresenter, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by presenter.models.collectAsStateWithLifecycle()
  val eventSink = presenter::eventSink

  ObserveEffects(presenter.effects) { effect ->
    when (effect) {
      NavigateUp -> navigator.pop()
      is OpenUrl -> navigator.goTo(Route.Url(effect.url))
      ResetToAuth -> navigator.resetRoot(Route.Auth)
    }
  }

  SettingsScreen(state, modifier, eventSink)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
  state: SettingsUiState,
  modifier: Modifier = Modifier,
  eventSink: (SettingsUiEvent) -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
  var showResetConfirmationDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeFlexibleTopAppBar(
        title = { Text(text = stringResource(Res.string.settings_title)) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
          IconButton(onClick = { eventSink(Close) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Default.ArrowBack,
              contentDescription = stringResource(Res.string.settings_navigate_back),
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
        LinkdingSettings(state = state, onResetClick = { showResetConfirmationDialog = true })
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
  if (showResetConfirmationDialog) {
    ResetConfirmationDialog(
      onConfirm = { eventSink(ResetApiConfig) },
      onDismiss = { eventSink(Close) },
    )
  }
}

private const val LinkdingSettingsCount = 3

@Composable
@Suppress("MagicNumber")
private fun LinkdingSettings(state: SettingsUiState, onResetClick: () -> Unit) {
  PreferenceSection(
    stringResource(Res.string.settings_linkding),
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Preference(
      shape = Material3ShapeDefaults.itemShape(0, LinkdingSettingsCount),
      title = stringResource(Res.string.settings_host_url),
      description = state.apiConfig?.hostUrl,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(1, LinkdingSettingsCount),
      title = stringResource(Res.string.settings_api_key),
      description = state.apiConfig?.apiKey,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(2, LinkdingSettingsCount),
      title = stringResource(Res.string.settings_reset),
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

  PreferenceSection(
    stringResource(Res.string.settings_appearance),
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    ThemePreference(
      shape = Material3ShapeDefaults.itemShape(0, AppearanceSettingsCount),
      selected = state.appTheme,
      onSelect = onAppThemeChange,
    )
    SwitchPreference(
      shape = Material3ShapeDefaults.itemShape(1, AppearanceSettingsCount),
      title = stringResource(Res.string.settings_dynamic_colours),
      description = stringResource(Res.string.settings_dynamic_colours_description),
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
  PreferenceSection(
    stringResource(Res.string.settings_about),
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Preference(
      shape = Material3ShapeDefaults.itemShape(0, AboutSettingsCount),
      title = stringResource(Res.string.settings_version),
      description = state.appInfo.version,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(1, AboutSettingsCount),
      title = stringResource(Res.string.settings_source_code),
      clickable = true,
      onClick = onSourceCodeClick,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(2, AboutSettingsCount),
      title = stringResource(Res.string.settings_licenses),
      clickable = true,
      onClick = onLicensesClick,
    )
    Preference(
      shape = Material3ShapeDefaults.itemShape(3, AboutSettingsCount),
      title = stringResource(Res.string.settings_privacy_policy),
      clickable = true,
      onClick = onPrivacyPolicyClick,
    )
  }
}

@Composable
private fun MadeInMunich() {
  Text(
    text = stringResource(Res.string.settings_made_in_munich),
    style = MaterialTheme.typography.bodyMedium,
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).navigationBarsPadding(),
    textAlign = TextAlign.Center,
  )
}

@Composable
fun ResetConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  AlertDialog(
    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Logout, "") },
    title = { Text(stringResource(Res.string.settings_confirm_reset_title)) },
    text = { Text(stringResource(Res.string.settings_confirm_reset_message)) },
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(onClick = onConfirm) { Text(stringResource(Res.string.settings_yes)) }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) { Text(stringResource(Res.string.settings_no)) }
    },
  )
}
