package dev.avatsav.linkding.android.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.extensions.circularReveal
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

data class SwipeAction(
    val onSwipe: () -> Unit,
    val background: Color,
    val canDismiss: Boolean = false,
    val content: @Composable BoxScope.(dismissing: Boolean) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SwipeableListItem(
    modifier: Modifier = Modifier,
    startAction: SwipeAction,
    endAction: SwipeAction,
    threshold: Dp = 64.dp,
    background: Color = Color.Transparent,
    content: @Composable (dismissState: DismissState) -> Unit,
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { false },
        positionalThreshold = { threshold.toPx() },
    )
    var dismissing: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(
        key1 = Unit,
        block = {
            snapshotFlow { dismissState.targetValue.willDismiss() }.distinctUntilChanged()
                .collect { dismissing = it }
        },
    )
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(
        key1 = dismissing,
        block = {
            if (dismissing) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        },
    )

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        background = {
            AnimatedContent(
                targetState = Pair(dismissState.dismissDirection, dismissing),
                transitionSpec = {
                    fadeIn(tween(220), if (targetState.second) 1f else 0f) with fadeOut(
                        tween(220),
                        if (targetState.second) 1f else 0f,
                    )
                },
            ) { (dismissDirection, dismissing) ->
                val revealSize = remember { Animatable(if (dismissing) 0.0f else 1f) }
                LaunchedEffect(
                    key1 = Unit,
                    block = {
                        if (dismissing) {
                            revealSize.snapTo(0f)
                            launch {
                                revealSize.animateTo(1f, animationSpec = tween(400))
                            }
                        }
                    },
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .circularReveal(
                            progress = revealSize.asState(),
                            centerOffset = Offset(
                                x = if (dismissDirection == DismissDirection.StartToEnd) 0f else 1f,
                                y = 0.5f,
                            ),
                        )
                        .background(
                            color = when (dismissDirection) {
                                DismissDirection.StartToEnd -> if (dismissing) startAction.background else background
                                DismissDirection.EndToStart -> if (dismissing) endAction.background else background
                                else -> background
                            },
                        ),
                ) {
                    val alignment: Alignment =
                        if (dismissDirection == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(alignment),
                    ) {
                        when (dismissDirection) {
                            DismissDirection.StartToEnd -> startAction.content(this, dismissing)
                            DismissDirection.EndToStart -> endAction.content(this, dismissing)
                            else -> {}
                        }
                    }
                }
            }
        },
        dismissContent = {
            content(dismissState)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun DismissValue.willDismiss(): Boolean {
    return this == DismissValue.DismissedToStart || this == DismissValue.DismissedToEnd
}
