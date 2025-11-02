package dev.avatsav.linkding.ui.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

fun appearFromBottom(): EnterTransition =
  slideInVertically(spring(Spring.DampingRatioMediumBouncy)) { it / 2 } + fadeIn()

fun disappearToBottom(): ExitTransition =
  slideOutVertically(spring(Spring.DampingRatioMediumBouncy)) { it / 2 } + fadeOut()
