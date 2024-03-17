package dev.avatsav.linkding.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.ui.SettingsScreen
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

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var selectedTheme by remember { mutableStateOf(AppTheme.System) }
    var dynamicColorsChecked by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Settings") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { eventSink(SettingsUiEvent.Close) }) {
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
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                PreferenceHeader("Appearance")
            }
            item {
                ThemePreference(
                    selected = selectedTheme,
                    onSelected = { selectedTheme = it },
                )
            }
            item {
                SwitchPreference(
                    title = "Dynamic colours",
                    description = "Stuff",
                    checked = dynamicColorsChecked,
                    onCheckedChange = { dynamicColorsChecked = dynamicColorsChecked.not() },
                )
            }
        }
    }
}
