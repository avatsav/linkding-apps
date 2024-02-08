package dev.avatsav.linkding.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.ui.SetupScreen
import dev.avatsav.linkding.ui.setup.SetupUiEvent.SaveConfiguration
import me.tatarka.inject.annotations.Inject

@Inject
class SetupUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is SetupScreen -> {
                ui<SetupUiState> { state, modifier ->
                    SetupScreen(state, modifier)
                }
            }

            else -> null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    state: SetupUiState,
    modifier: Modifier = Modifier,
) {
    var hostUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    val errorMessage =
        if (state.invalidApiKey) {
            "Invalid API Key"
        } else if (state.invalidHostUrl) {
            "Unable to reach host"
        } else {
            ""
        }

    Scaffold(
        topBar = { LargeTopAppBar(title = { Text(text = "Setup Linkding") }) },
    ) { padding ->

        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val allFieldsFilledOut = hostUrl.isNotEmpty() && apiKey.isNotEmpty()

            Text(text = "Configure settings, so that the app can communicate with your linkding installation.")
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = hostUrl,
                enabled = !state.loading,
                singleLine = true,
                label = { Text(text = "Host URL") },
                isError = state.invalidHostUrl,
                supportingText = {
                    if (errorMessage.isNotBlank() && state.invalidHostUrl) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                onValueChange = { hostUrl = it },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = apiKey,
                enabled = !state.loading,
                singleLine = true,
                label = { Text(text = "API Key") },
                isError = state.invalidApiKey,
                supportingText = {
                    if (errorMessage.isNotBlank() && state.invalidApiKey) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                onValueChange = { apiKey = it },
            )
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    enabled = allFieldsFilledOut && !state.loading,
                    onClick = { state.eventSink(SaveConfiguration(hostUrl, apiKey)) },
                ) {
                    Text("Save")
                }
                if (state.loading) CircularProgressIndicator()
            }
        }
    }
}
