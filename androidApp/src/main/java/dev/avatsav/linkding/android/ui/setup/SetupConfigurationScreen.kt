package dev.avatsav.linkding.android.ui.setup

import android.content.res.Configuration
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.SetupConfigurationPresenter
import dev.avatsav.linkding.ui.SetupConfigurationPresenter.ViewState
import dev.avatsav.linkding.ui.SetupConfigurationPresenter.ViewState.Error

@Composable
fun SetupConfigurationScreen(presenter: SetupConfigurationPresenter) {
    val uiState by presenter.uiState.collectAsState()
    DisposableEffect(presenter) {
        onDispose {
            presenter.clear()
        }
    }
    SetupConfigurationScreen(uiState = uiState, onSubmitted = { url, apiKey ->
        presenter.setConfiguration(url, apiKey)
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupConfigurationScreen(
    modifier: Modifier = Modifier, uiState: ViewState, onSubmitted: (String, String) -> Unit
) {
    var url by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Hello there!", style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Enter details and start bookmarking", style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.size(24.dp))
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = url,
            enabled = !uiState.loading,
            label = { Text(text = "Linkding Host URL") },
            isError = uiState.error is Error.UrlEmpty,
            supportingText = {
                AnimatedVisibility(visible = uiState.error is Error.UrlEmpty) {
                    Text(text = uiState.error.message)
                }
            },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next,
            ),
            onValueChange = { value ->
                url = value
            })
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = apiKey,
            enabled = !uiState.loading,
            label = { Text(text = "API Key") },
            isError = uiState.error is Error.ApiKeyEmpty,
            supportingText = {
                AnimatedVisibility(visible = uiState.error is Error.ApiKeyEmpty) {
                    Text(text = uiState.error.message)
                }
            },
            onValueChange = { value ->
                apiKey = value
            })
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(enabled = !uiState.loading, onClick = {
                onSubmitted(url, apiKey)
            }) {
                Text("Let's go")
            }
            if (uiState.loading) {
                CircularProgressIndicator(
                    color = Color.White
                )
            }
            AnimatedVisibility(visible = uiState.error is Error.CannotConnect) {
                Text(
                    text = uiState.error.message,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SetupConfigurationScreen_Preview() {
    LinkdingTheme {
        SetupConfigurationScreen(uiState = ViewState(
            loading = false,
            error = Error.None,
        ), onSubmitted = { _, _ -> })
    }
}