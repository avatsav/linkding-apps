package dev.avatsav.linkding.android.ui.home

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.AsyncState
import dev.avatsav.linkding.ui.Loading
import dev.avatsav.linkding.ui.presenter.SaveConfigurationError
import dev.avatsav.linkding.ui.Uninitialized
import dev.avatsav.linkding.ui.getError
import dev.avatsav.linkding.ui.onLoading
import dev.avatsav.linkding.ui.onSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupConfigurationScreen(
    modifier: Modifier = Modifier,
    state: AsyncState<Unit, SaveConfigurationError>,
    onSaveSuccess: () -> Unit,
    onConfigurationSubmitted: (String, String) -> Unit
) {
    var url by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    val urlEmptyError = state.getError() as? SaveConfigurationError.UrlEmpty
    val apiKeyEmptyError = state.getError() as? SaveConfigurationError.ApiKeyEmpty
    val cannotConnectError = state.getError() as? SaveConfigurationError.CannotConnect

    state.onSuccess {
        onSaveSuccess()
    }

    Scaffold(topBar = {
        LargeTopAppBar(title = { Text(text = "Setup Linkding") })
    }) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Configure settings, so that the app can communicate with your linkding installation.")
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = url,
                enabled = state !is Loading,
                label = { Text(text = "Linkding Host URL") },
                isError = urlEmptyError != null,
                supportingText = {
                    AnimatedVisibility(visible = urlEmptyError != null) {
                        if (urlEmptyError != null) Text(text = urlEmptyError.message)
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
                enabled = state !is Loading,
                label = { Text(text = "API Key") },
                isError = apiKeyEmptyError != null,
                supportingText = {
                    AnimatedVisibility(visible = apiKeyEmptyError != null) {
                        if (apiKeyEmptyError != null) Text(text = apiKeyEmptyError.message)
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
                Button(
                    enabled = state !is Loading,
                    onClick = { onConfigurationSubmitted(url, apiKey) })
                {
                    Text("Save")
                }
                state onLoading {
                    CircularProgressIndicator()
                }
                AnimatedVisibility(visible = cannotConnectError != null) {
                    if (cannotConnectError != null) Text(
                        text = cannotConnectError.message,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SetupConfigurationScreen_Preview() {
    LinkdingTheme {
        SetupConfigurationScreen(state = Uninitialized,
            onSaveSuccess = {},
            onConfigurationSubmitted = { _, _ -> })
    }
}