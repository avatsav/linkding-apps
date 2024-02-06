package dev.avatsav.linkding.ui.setup

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.avatsav.linkding.ui.SetupScreen
import me.tatarka.inject.annotations.Inject

@Inject
class SetupUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is SetupScreen -> {
                ui<SetupUiState> { state, modifier ->
                    SetupApiConfiguration(state, modifier)
                }
            }

            else -> null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupApiConfiguration(
    state: SetupUiState,
    modifier: Modifier = Modifier,
) {
    var hostUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    var hostUrlErrorMessage by remember { mutableStateOf("") }
    var apiKeyErrorMessage by remember { mutableStateOf("") }

    if (state.invalidApiKey) apiKeyErrorMessage = "Invalid API Key"
    if (state.invalidHostUrl) hostUrlErrorMessage = "Unable to reach host"

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = "Setup Linkding") })
        },
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Configure settings, so that the app can communicate with your linkding installation.")
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = hostUrl,
                enabled = !state.loading,
                singleLine = true,
                label = { Text(text = "Linkding Host URL") },
                isError = hostUrlErrorMessage.isNotBlank(),
                supportingText = {
                    if (hostUrlErrorMessage.isNotBlank()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = hostUrlErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                onValueChange = { value ->
                    if (value.isNotEmpty()) hostUrlErrorMessage = ""
                    hostUrl = value
                },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = apiKey,
                enabled = !state.loading,
                singleLine = true,
                label = { Text(text = "API Key") },
                isError = apiKeyErrorMessage.isNotBlank(),
                supportingText = {
                    if (apiKeyErrorMessage.isNotBlank()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = apiKeyErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                onValueChange = { value ->
                    if (value.isNotEmpty()) apiKeyErrorMessage = ""
                    apiKey = value
                },
            )
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    enabled = !state.loading,
                    onClick = {
                        if (hostUrl.isNotBlank() && apiKey.isNotBlank()) {
                            state.eventSink(SetupUiEvent.SaveConfiguration(hostUrl, apiKey))
                        }
                        if (hostUrl.isEmpty()) hostUrlErrorMessage = "Cannot be empty."
                        if (apiKey.isEmpty()) apiKeyErrorMessage = "Cannot be empty."
                    },
                ) {
                    Text("Save")
                }
                if (state.loading) CircularProgressIndicator()
            }
            AnimatedVisibility(visible = state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
