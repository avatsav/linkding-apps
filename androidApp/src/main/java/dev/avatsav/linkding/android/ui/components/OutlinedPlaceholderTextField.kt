package dev.avatsav.linkding.android.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.extensions.onCondition
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedPlaceholderTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box(modifier = modifier.onCondition(label != null) { padding(top = 4.dp) }) {
        OutlinedPlaceholderTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            placeholder = placeholder,
            decorationBox = { innerTextField ->
                TextFieldDefaults.OutlinedTextFieldDecorationBox(
                    value = if (value.isBlank() && placeholder != null) " " else "",
                    enabled = true,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = false,
                    trailingIcon = trailingIcon,
                    label = label,
                    supportingText = supportingText,
                    innerTextField = innerTextField,
                )
            },
        )
    }
}

@Composable
private fun OutlinedPlaceholderTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() },
) {
    val textFieldFocusRequester = remember { FocusRequester() }
    val placeholderVisible = value.isBlank() && placeholder != null
    decorationBox {
        Box(modifier = modifier
            .fillMaxWidth()
            .pointerInput(value) {
                detectTapGestures(
                    onTap = {
                        textFieldFocusRequester.requestFocus()
                    },
                )
            }) {
            AnimatedVisibility(
                visible = placeholderVisible,
                enter = fadeIn(keyframes { durationMillis = 400 }),
                exit = fadeOut(keyframes { durationMillis = 400 }),
            ) {
                if (placeholder != null) Decoration(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    typography = MaterialTheme.typography.bodyLarge,
                    content = placeholder
                )
            }

        }
        BasicTextField(
            modifier = Modifier.focusRequester(textFieldFocusRequester),
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            interactionSource = interactionSource,
        )
    }
}


// Taken from Material3
@Composable
internal fun Decoration(
    contentColor: Color, typography: TextStyle? = null, content: @Composable () -> Unit
) {
    val contentWithColor: @Composable () -> Unit = @Composable {
        CompositionLocalProvider(
            LocalContentColor provides contentColor, content = content
        )
    }
    if (typography != null) ProvideTextStyle(typography, contentWithColor) else contentWithColor()
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OutlinedPlaceholderTextField_Preview() {
    LinkdingTheme {
        Surface {
            OutlinedPlaceholderTextField(value = "",
                onValueChange = {},
                label = { Text("Title") },
                placeholder = {
                    Text(
                        "Placeholder text", color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                })
        }
    }
}


