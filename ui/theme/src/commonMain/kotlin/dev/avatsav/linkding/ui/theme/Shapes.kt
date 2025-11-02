package dev.avatsav.linkding.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object Material3ShapeDefaults {
  private val baseShape: CornerBasedShape
    get() = RoundedCornerShape(4.dp)

  private val singleItemShape: CornerBasedShape
    get() = RoundedCornerShape(12.dp)

  @Composable
  @ReadOnlyComposable
  fun itemShape(
    index: Int,
    count: Int,
    baseShape: CornerBasedShape = Material3ShapeDefaults.baseShape,
  ): Shape {
    if (count == 1) return singleItemShape
    return when (index) {
      0 -> baseShape.start()
      count - 1 -> baseShape.end()
      else -> baseShape
    }
  }
}

private fun CornerBasedShape.start(): CornerBasedShape =
  copy(topStart = CornerSize(12.dp), topEnd = CornerSize(12.dp))

private fun CornerBasedShape.end(): CornerBasedShape =
  copy(bottomStart = CornerSize(12.dp), bottomEnd = CornerSize(12.dp))
