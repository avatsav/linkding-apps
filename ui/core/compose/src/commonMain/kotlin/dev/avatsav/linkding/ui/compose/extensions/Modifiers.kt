package dev.avatsav.linkding.ui.compose.extensions

import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.sqrt

fun Modifier.onCondition(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return then(if (condition) modifier() else this)
}

fun Modifier.circularReveal(progress: State<Float>, centerOffset: Offset): Modifier {
    return drawWithCache {
        val center = Offset(centerOffset.x * size.width, centerOffset.y * size.height)
        val radius = sqrt(size.width * size.width + size.height * size.height) * progress.value
        onDrawWithContent {
            clipPath(
                Path().apply { addOval(Rect(center, radius)) },
            ) {
                this@onDrawWithContent.drawContent()
            }
        }
    }
}
