package dev.avatsav.linkding.ui.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import dev.avatsav.linkding.ui.extensions.circularReveal
import kotlinx.coroutines.flow.distinctUntilChanged

@Stable
data class SwipeToDismissAction(
    val onSwipeToDismissTriggered: () -> Unit,
    val backgroundColour: Color,
    val canDismiss: Boolean,
    val content: @Composable BoxScope.(dismissing: Boolean) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissListItem(
    modifier: Modifier = Modifier,
    startToEndAction: SwipeToDismissAction,
    endToStartAction: SwipeToDismissAction,
    backgroundColor: Color = Color.Transparent,
    content: @Composable (dismissState: SwipeToDismissBoxState) -> Unit,
) {
    val dismissState = rememberNoFlingSwipeToDismissBoxState(
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

@OptIn(ExperimentalMaterial3Api::class)
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
    LaunchedEffect(Unit) {
        snapshotFlow { dismissState.targetValue.willDismiss() }.distinctUntilChanged()
            .collect { dismissing = it }
    }
    LaunchedEffect(dismissing) {
        if (dismissing) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    AnimatedContent(
        targetState = Pair(dismissState.dismissDirection, dismissing),
        transitionSpec = {
            fadeIn(tween(220), if (targetState.second) 1f else 0f) togetherWith fadeOut(
                tween(220),
                if (targetState.second) 1f else 0f,
            )
        },
    ) { (dismissDirection, dismissing) ->
        val revealSize = remember { Animatable(if (dismissing) 0.0f else 1f) }
        LaunchedEffect(dismissing) {
            if (dismissing) {
                revealSize.snapTo(0f)
                revealSize.animateTo(1f, tween(400))
            }
        }
        val activatedColor = when (dismissDirection) {
            StartToEnd -> if (dismissing) startAction.backgroundColour else backgroundColor
            EndToStart -> if (dismissing) endAction.backgroundColour else backgroundColor
            else -> backgroundColor
        }
        Box(
            Modifier.fillMaxSize()
                .circularReveal(
                    progress = revealSize.asState(),
                    centerOffset = Offset(
                        x = if (dismissDirection == StartToEnd) 0.1f else 0.9f,
                        y = 0.5f,
                    ),
                ).background(activatedColor),
        ) {
            val alignment: Alignment = when (dismissDirection) {
                StartToEnd -> Alignment.CenterStart
                else -> Alignment.CenterEnd
            }
            Box(Modifier.fillMaxHeight().align(alignment)) {
                when (dismissDirection) {
                    StartToEnd -> startAction.content(this, dismissing)
                    EndToStart -> endAction.content(this, dismissing)
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeToDismissBoxValue.willDismiss(): Boolean {
    return this == StartToEnd || this == EndToStart
}


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
        saver = SwipeToDismissBoxState.Saver(
            confirmValueChange = confirmValueChange,
            density = density,
            positionalThreshold = positionalThreshold,
        ),
    ) {
        SwipeToDismissBoxState(initialValue, density, confirmValueChange, positionalThreshold)
    }
}
