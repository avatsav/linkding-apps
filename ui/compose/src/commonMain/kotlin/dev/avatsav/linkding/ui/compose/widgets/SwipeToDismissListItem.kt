package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Density
import dev.avatsav.linkding.ui.compose.circularReveal
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissListItem(
  startToEndAction: SwipeToDismissAction,
  endToStartAction: SwipeToDismissAction,
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.Transparent,
  content: @Composable (dismissState: SwipeToDismissBoxState) -> Unit,
) {
  val dismissState =
    rememberNoFlingSwipeToDismissBoxState(
      confirmValueChange = { dismissValue ->
        when (dismissValue) {
          StartToEnd -> startToEndAction.canDismiss
          EndToStart -> endToStartAction.canDismiss
          Settled -> true
        }
      },
      positionalThreshold = { totalDistance -> 0.35f * totalDistance },
    )

  LaunchedEffect(dismissState.currentValue) {
    when (dismissState.currentValue) {
      StartToEnd -> startToEndAction.onSwipeToDismissTriggered()
      EndToStart -> endToStartAction.onSwipeToDismissTriggered()
      Settled -> {}
    }
  }

  SwipeToDismissBox(
    state = dismissState,
    backgroundContent = {
      SwipeDismissBackgroundContent(
        dismissState,
        startToEndAction,
        endToStartAction,
        backgroundColor,
      )
    },
    modifier = modifier,
    enableDismissFromStartToEnd = true,
    enableDismissFromEndToStart = true,
    content = { content(dismissState) },
  )
}

private const val RevealDuration = 400

@Composable
private fun SwipeDismissBackgroundContent(
  dismissState: SwipeToDismissBoxState,
  startAction: SwipeToDismissAction,
  endAction: SwipeToDismissAction,
  backgroundColor: Color = Color.Transparent,
) {
  if (dismissState.dismissDirection == Settled) return

  val haptics = LocalHapticFeedback.current
  var dismissing: Boolean by remember { mutableStateOf(false) }
  var dismissDirection: SwipeToDismissBoxValue by remember { mutableStateOf(StartToEnd) }
  val revealSize by remember { derivedStateOf { Animatable(if (dismissing) 0f else 1f) } }
  val activeColor by remember {
    derivedStateOf {
      when (dismissDirection) {
        StartToEnd -> if (dismissing) startAction.backgroundColour else backgroundColor
        EndToStart -> if (dismissing) endAction.backgroundColour else backgroundColor
        else -> backgroundColor
      }
    }
  }
  val revealCenterOffset by remember {
    derivedStateOf {
      if (dismissDirection == StartToEnd) Offset(x = 0.1f, y = 0.5f) else Offset(x = 0.9f, y = 0.5f)
    }
  }
  val swipeActionAlignment by remember {
    derivedStateOf {
      if (dismissDirection == StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
    }
  }

  LaunchedEffect(Unit) {
    launch {
      snapshotFlow { dismissState.targetValue.willDismiss() }
        .distinctUntilChanged()
        .collect {
          dismissing = it
          if (dismissing) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            revealSize.snapTo(0f)
            revealSize.animateTo(1f, tween(RevealDuration))
          }
        }
    }
    launch {
      snapshotFlow { dismissState.dismissDirection }
        .distinctUntilChanged()
        .collect { dismissDirection = it }
    }
  }

  Box(
    Modifier.fillMaxSize()
      .circularReveal(revealSize.asState(), revealCenterOffset)
      .background(activeColor)
  ) {
    Box(Modifier.fillMaxHeight().align(swipeActionAlignment)) {
      if (dismissDirection == StartToEnd) startAction.content(this, dismissing)
      else endAction.content(this, dismissing)
    }
  }
}

private fun SwipeToDismissBoxValue.willDismiss(): Boolean = this == StartToEnd || this == EndToStart

// https://issuetracker.google.com/issues/252334353#comment16
@Composable
@ExperimentalMaterial3Api
fun rememberNoFlingSwipeToDismissBoxState(
  initialValue: SwipeToDismissBoxValue = Settled,
  confirmValueChange: (SwipeToDismissBoxValue) -> Boolean = { true },
  positionalThreshold: (totalDistance: Float) -> Float =
    SwipeToDismissBoxDefaults.positionalThreshold,
): SwipeToDismissBoxState {
  val density = Density(Float.POSITIVE_INFINITY)
  return rememberSaveable(
    saver =
      SwipeToDismissBoxState.Saver(
        confirmValueChange = confirmValueChange,
        density = density,
        positionalThreshold = positionalThreshold,
      )
  ) {
    SwipeToDismissBoxState(initialValue, density, confirmValueChange, positionalThreshold)
  }
}

@Stable
data class SwipeToDismissAction(
  val onSwipeToDismissTriggered: () -> Unit,
  val backgroundColour: Color,
  val canDismiss: Boolean,
  val content: @Composable BoxScope.(dismissing: Boolean) -> Unit,
)
