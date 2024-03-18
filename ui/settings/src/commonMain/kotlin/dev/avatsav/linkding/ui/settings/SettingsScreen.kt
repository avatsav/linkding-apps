package dev.avatsav.linkding.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import com.slack.circuitx.overlays.DialogResult
import com.slack.circuitx.overlays.alertDialogOverlay
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.settings.SettingsUiEvent.Close
import dev.avatsav.linkding.ui.settings.SettingsUiEvent.ResetApiConfig
import dev.avatsav.linkding.ui.settings.SettingsUiEvent.SetAppTheme
import dev.avatsav.linkding.ui.settings.SettingsUiEvent.ToggleUseDynamicColors
import dev.avatsav.linkding.ui.settings.widgets.Preference
import dev.avatsav.linkding.ui.settings.widgets.PreferenceDefaults
import dev.avatsav.linkding.ui.settings.widgets.PreferenceSection
import dev.avatsav.linkding.ui.settings.widgets.SwitchPreference
import dev.avatsav.linkding.ui.settings.widgets.ThemePreference
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is SettingsScreen -> {
                ui<SettingsUiState> { state, modifier ->
                    Settings(state, modifier)
                }
            }

            else -> null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    state: SettingsUiState,
    modifier: Modifier = Modifier,
) {
    // https://issuetracker.google.com/issues/256100927#comment1
    val eventSink = state.eventSink
    val overlayHost = LocalOverlayHost.current

    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
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
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                PreferenceSection("Linkding", modifier = Modifier.padding(vertical = 8.dp)) {
                    Preference(
                        shape = PreferenceDefaults.itemShape(0, 3),
                        title = "Host Url",
                        description = state.apiConfig?.hostUrl,
                    )
                    Preference(
                        shape = PreferenceDefaults.itemShape(1, 3),
                        title = "API Key",
                        description = state.apiConfig?.apiKey,
                    )
                    Preference(
                        shape = PreferenceDefaults.itemShape(2, 3),
                        title = "Reset",
                        clickable = true,
                        onClicked = {
                            coroutineScope.launch {
                                val result = overlayHost.showResetConfirmationDialog()
                                if (result == DialogResult.Confirm) eventSink(ResetApiConfig)
                            }
                        },
                    )
                }
            }
            item {
                PreferenceSection("Appearance", modifier = Modifier.padding(vertical = 8.dp)) {
                    ThemePreference(
                        shape = PreferenceDefaults.itemShape(0, 2),
                        selected = state.appTheme,
                        onSelected = { if (state.appTheme != it) eventSink(SetAppTheme(it)) },
                    )
                    SwitchPreference(
                        shape = PreferenceDefaults.itemShape(1, 2),
                        title = "Dynamic colours",
                        description = "Colors adapt to your wallpaper",
                        checked = state.useDynamicColors,
                        onCheckedChange = { eventSink(ToggleUseDynamicColors) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Inject
suspend fun OverlayHost.showResetConfirmationDialog(): DialogResult {
    return show(
        alertDialogOverlay(
            title = { Text("Confirm Reset") },
            text = { Text("Are you sure you want to reset the api configuration?") },
            confirmButton = { onClick ->
                Button(onClick = onClick) { Text("Yes") }
            },
            dismissButton = { onClick ->
                OutlinedButton(onClick = onClick) { Text("No") }
            },
        ),
    )
}
