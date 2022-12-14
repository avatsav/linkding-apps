package dev.avatsav.linkding.android.ui.setup

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupCredentials(
    modifier: Modifier = Modifier,
    onSubmitted: (String, String) -> Unit
) {

    var loading by rememberSaveable { mutableStateOf(false) }
    var hostUrl by rememberSaveable { mutableStateOf("") }
    var apiKey by rememberSaveable { mutableStateOf("") }
    var hostUrlError by remember { mutableStateOf("") }
    var apiKeyError by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = "Hello there!", style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Enter details and start bookmarking", style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.size(24.dp))
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = hostUrl,
            enabled = !loading,
            label = { Text(text = "Linkding Host URL") },
            isError = hostUrlError.isNotBlank(),
            supportingText = { Text(text = hostUrlError) },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next,
            ),
            onValueChange = { value ->
                hostUrlError = ""
                hostUrl = value
            })
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = apiKey,
            enabled = !loading,
            label = { Text(text = "API Key") },
            isError = apiKeyError.isNotBlank(),
            supportingText = { Text(text = apiKeyError) },
            onValueChange = { value ->
                apiKeyError = ""
                apiKey = value
            })
        Spacer(modifier = Modifier.size(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(
                enabled = !loading,

                onClick = {
                    when {
                        hostUrl.isEmpty() -> {
                            hostUrlError = "URL cannot be empty"
                            return@Button
                        }

                        apiKey.isEmpty() -> {
                            apiKeyError = "API key cannot be empty"
                            return@Button
                        }
                    }
                    loading = true
                    onSubmitted(hostUrl, apiKey)
                }) {
                Text("Let's go")
            }
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SetupPreview() {
    LinkdingTheme {
        SetupCredentials(onSubmitted = { _, _ -> })
    }
}