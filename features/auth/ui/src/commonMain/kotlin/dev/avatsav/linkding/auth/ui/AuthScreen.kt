package dev.avatsav.linkding.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.avatsav.linkding.auth.ui.AuthUiEvent.SaveCredentials
import dev.avatsav.linkding.ui.AuthScreen
import dev.avatsav.linkding.ui.compose.widgets.SmallCircularProgressIndicator
import dev.zacsweers.metro.AppScope

@CircuitInject(AuthScreen::class, AppScope::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(state: AuthUiState, modifier: Modifier = Modifier) {
  // https://issuetracker.google.com/issues/256100927#comment1
  val eventSink = state.eventSink

  var hostUrl by remember { mutableStateOf("") }
  var apiKey by remember { mutableStateOf("") }
  val allFieldsFilledOut by remember {
    derivedStateOf { hostUrl.isNotEmpty() && apiKey.isNotEmpty() }
  }

  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

  val errorMessage =
    if (state.invalidApiKey) {
      "Invalid API Key"
    } else if (state.invalidHostUrl) {
      "Unable to reach host"
    } else {
      ""
    }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeFlexibleTopAppBar(
        title = { Text(text = "Setup Linkding") },
        scrollBehavior = scrollBehavior,
      )
    },
    contentWindowInsets = WindowInsets(),
  ) { padding ->
    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
      // Scrollable content
      Column(
        modifier =
          Modifier.fillMaxWidth().padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
                text =
                        "Configure settings, so that the app can communicate with your linkding installation.",
        )
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
          keyboardOptions =
            KeyboardOptions(
              autoCorrectEnabled = false,
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
      }

      // Docked save button, positioned above keyboard
      BottomAppBar(
        modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().imePadding(),
        floatingActionButton = {
          Button(
            modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 48.dp),
            enabled = allFieldsFilledOut && !state.loading,
            onClick = { eventSink(SaveCredentials(hostUrl, apiKey)) },
          ) {
            if (state.loading) SmallCircularProgressIndicator() else Text("Save")
          }
        },
        actions = {},
      )
    }
  }
}
