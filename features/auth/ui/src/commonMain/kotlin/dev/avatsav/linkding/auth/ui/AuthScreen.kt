package dev.avatsav.linkding.auth.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.auth.ui.AuthUiEvent.SaveCredentials
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.presenter.ObserveEffects
import dev.avatsav.linkding.ui.compose.rememberLocalNetworkPermissionRequest
import dev.avatsav.linkding.ui.compose.widgets.SmallCircularProgressIndicator
import linkding_apps.features.auth.ui.generated.resources.Res
import linkding_apps.features.auth.ui.generated.resources.auth_allow_access
import linkding_apps.features.auth.ui.generated.resources.auth_api_key
import linkding_apps.features.auth.ui.generated.resources.auth_description
import linkding_apps.features.auth.ui.generated.resources.auth_error_invalid_api_key
import linkding_apps.features.auth.ui.generated.resources.auth_error_invalid_host
import linkding_apps.features.auth.ui.generated.resources.auth_hide_api_key
import linkding_apps.features.auth.ui.generated.resources.auth_host_url
import linkding_apps.features.auth.ui.generated.resources.auth_local_network_required
import linkding_apps.features.auth.ui.generated.resources.auth_open_app_settings
import linkding_apps.features.auth.ui.generated.resources.auth_save
import linkding_apps.features.auth.ui.generated.resources.auth_show_api_key
import linkding_apps.features.auth.ui.generated.resources.auth_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(presenter: AuthPresenter, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by presenter.models.collectAsStateWithLifecycle()
  val eventSink = presenter::eventSink
  val permissionRequest = rememberLocalNetworkPermissionRequest {
    eventSink(AuthUiEvent.LocalNetworkPermissionResult(it))
  }

  ObserveEffects(presenter.effects) { effect ->
    when (effect) {
      AuthEffect.AuthenticationSuccess -> navigator.resetRoot(Route.BookmarksFeed)
      AuthEffect.RequestLocalNetworkAccess -> permissionRequest.launch()
    }
  }

  AuthScreen(state, modifier, permissionRequest.opensSettings, eventSink)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AuthScreen(
  state: AuthUiState,
  modifier: Modifier = Modifier,
  opensSettings: Boolean = false,
  eventSink: (AuthUiEvent) -> Unit,
) {

  var hostUrl by rememberSaveable { mutableStateOf("") }
  var apiKey by rememberSaveable { mutableStateOf("") }
  var apiKeyVisible by remember { mutableStateOf(false) }
  val allFieldsFilledOut by remember {
    derivedStateOf { hostUrl.isNotEmpty() && apiKey.isNotEmpty() }
  }

  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

  val errorMessage =
    if (state.invalidApiKey) {
      stringResource(Res.string.auth_error_invalid_api_key)
    } else if (state.invalidHostUrl) {
      stringResource(Res.string.auth_error_invalid_host)
    } else {
      ""
    }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeFlexibleTopAppBar(
        title = { Text(text = stringResource(Res.string.auth_title)) },
        scrollBehavior = scrollBehavior,
      )
    },
    bottomBar = {
      BottomAppBar(
        modifier = Modifier.imePadding(),
        floatingActionButton = {
          Button(
            modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 48.dp),
            enabled = allFieldsFilledOut && !state.loading,
            onClick = { eventSink(SaveCredentials(hostUrl, apiKey)) },
          ) {
            if (state.loading) SmallCircularProgressIndicator()
            else Text(stringResource(Res.string.auth_save))
          }
        },
        actions = {},
      )
    },
    contentWindowInsets = WindowInsets(0.dp),
  ) { padding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(padding)
          .padding(horizontal = 16.dp)
          .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(text = stringResource(Res.string.auth_description))
      if (state.localNetworkAccessRequired) {
        Text(text = stringResource(Res.string.auth_local_network_required))
        TextButton(
          enabled = allFieldsFilledOut && !state.loading,
          onClick = { eventSink(SaveCredentials(hostUrl, apiKey)) },
        ) {
          Text(
            stringResource(
              if (opensSettings) Res.string.auth_open_app_settings else Res.string.auth_allow_access
            )
          )
        }
      }
      state.errorMessage
        ?.takeIf { it.isNotBlank() }
        ?.let { message -> Text(text = message, color = MaterialTheme.colorScheme.error) }
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = hostUrl,
        enabled = !state.loading,
        singleLine = true,
        label = { Text(text = stringResource(Res.string.auth_host_url)) },
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
        modifier = Modifier.fillMaxWidth().semantics { contentType = ContentType.Password },
        value = apiKey,
        enabled = !state.loading,
        singleLine = true,
        label = { Text(text = stringResource(Res.string.auth_api_key)) },
        visualTransformation =
          if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions =
          KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
          ),
        trailingIcon = {
          ApiKeyVisibilityToggle(
            visible = apiKeyVisible,
            enabled = !state.loading,
            onClick = { apiKeyVisible = !apiKeyVisible },
          )
        },
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
  }
}

@Composable
private fun ApiKeyVisibilityToggle(
  visible: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
    Icon(
      imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
      contentDescription =
        stringResource(if (visible) Res.string.auth_hide_api_key else Res.string.auth_show_api_key),
    )
  }
}
