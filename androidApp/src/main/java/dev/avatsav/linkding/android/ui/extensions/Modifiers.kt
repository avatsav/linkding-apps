package dev.avatsav.linkding.android.ui.extensions

import androidx.compose.ui.Modifier

fun Modifier.onCondition(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return then(if (condition) modifier() else this)
}

