package dev.avatsav.linkding.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.avatsav.linkding.ui.SplashScreen
import dev.zacsweers.metro.AppScope

@CircuitInject(screen = SplashScreen::class, scope = AppScope::class)
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
  Scaffold(
    modifier = modifier,
    content = { padding ->
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
          Modifier.padding(padding).background(MaterialTheme.colorScheme.surface).fillMaxSize(),
      ) {
        CircularProgressIndicator()
      }
    },
  )
}
