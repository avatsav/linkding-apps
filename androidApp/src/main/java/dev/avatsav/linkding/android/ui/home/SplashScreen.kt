package dev.avatsav.linkding.android.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.R
import dev.avatsav.linkding.android.theme.LinkdingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen() {
    Scaffold(content = { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_android),
                    modifier = Modifier
                        .height(120.dp)
                        .width(120.dp),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            CircularProgressIndicator(
                modifier = Modifier.padding(vertical = 36.dp)
            )
            Text(
                text = "Linkding",
                style = MaterialTheme.typography.titleLarge
            )
        }
    })
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SplashScreen_Preview() {
    LinkdingTheme {
        SplashScreen()
    }
}