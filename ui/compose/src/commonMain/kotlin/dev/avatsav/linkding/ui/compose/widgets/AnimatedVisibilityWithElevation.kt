package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * An [AnimatedVisibility] composable that also animates elevation after the enter transition
 * completes.
 *
 * @param visible Controls the visibility of the content.
 * @param modifier The modifier to be applied to the AnimatedVisibility.
 * @param targetElevation The elevation value to animate to after the enter transition completes.
 * @param enter The enter transition to use when the content becomes visible.
 * @param exit The exit transition to use when the content becomes invisible.
 * @param animationSpec The animation spec to use for the elevation animation.
 * @param content The content lambda that receives the animated elevation value.
 */
@Composable
fun AnimatedVisibilityWithElevation(
  visible: Boolean,
  modifier: Modifier = Modifier,
  targetElevation: Dp = 6.dp,
  enter: EnterTransition = fadeIn(),
  exit: ExitTransition = fadeOut(),
  animationSpec: AnimationSpec<Dp> = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
  content: @Composable (elevation: Dp) -> Unit,
) {
  val visibleState = remember { MutableTransitionState(false) }
  visibleState.targetState = visible

  val elevation by
    animateDpAsState(
      targetValue = if (visibleState.currentState && visibleState.isIdle) targetElevation else 0.dp,
      animationSpec = animationSpec,
    )

  AnimatedVisibility(modifier = modifier, visibleState = visibleState, enter = enter, exit = exit) {
    content(elevation)
  }
}
