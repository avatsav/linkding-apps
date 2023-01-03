package dev.avatsav.linkding.android.ui.components.tags

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import dev.avatsav.linkding.android.ui.extensions.onCondition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsTextField(
    modifier: Modifier = Modifier,
    value: TagsTextFieldValue,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    Box(modifier = modifier.onCondition(label != null) { padding(top = 4.dp) }) {
        TagsTextField(
            tagsValue = value,
            textValue = textValue,
            onTextValueChange = { textValue = it },
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                TextFieldDefaults.OutlinedTextFieldDecorationBox(
                    value = if (value.tags.isEmpty() && textValue.text.isEmpty()) "" else " ",
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = false,
                    label = label,
                    supportingText = supportingText
                )
            },
        )
    }
}


@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
private fun TagsTextField(
    modifier: Modifier = Modifier,
    tagsValue: TagsTextFieldValue,
    textValue: TextFieldValue,
    onTextValueChange: (TextFieldValue) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() },
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldFocusRequester = remember { FocusRequester() }

    decorationBox {
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(textValue) {
                    detectTapGestures(
                        onTap = {
                            keyboardController?.show()
                            textFieldFocusRequester.requestFocus()
                        },
                    )
                }, mainAxisSpacing = 4.dp, crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {

            for (tag in tagsValue.tags) {
                InputChip(selected = false,
                    onClick = { /*TODO*/ },
                    label = { Text(tag.value) },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.size(16.dp),
                            onClick = { tagsValue.removeTag(tag) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    })
            }
            BasicTextField(
                value = textValue,
                modifier = Modifier
                    .focusRequester(textFieldFocusRequester)
                    .onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                            if (textValue.text.isEmpty() && tagsValue.tags.isNotEmpty()) {
                                tagsValue.removeLastTag()
                                return@onPreviewKeyEvent true
                            }
                        }
                        false
                    },
                onValueChange = taggableValueChange(
                    onTag = { tagText ->
                        tagsValue.addTag(Tag(tagText))
                    }, onValueChange = onTextValueChange
                ),
                textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    val tagText = textValue.text
                    if (tagText.isNotEmpty()) {
                        tagsValue.addTag(Tag(tagText))
                        onTextValueChange(TextFieldValue())
                    }
                }),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                interactionSource = interactionSource,
            )
        }
    }

}

@Stable
data class Tag(val value: String)

@Stable
class TagsTextFieldValue(tags: List<Tag> = emptyList()) {

    var tags by mutableStateOf(tags)

    fun addTag(tag: Tag) {
        val list = tags.toMutableList()
        list.add(tag)
        tags = list
    }

    fun removeTag(tag: Tag) {
        val list = tags.toMutableList()
        val index = list.indexOf(tag)
        if (index == -1) return
        list.remove(tag)
        tags = list
    }

    internal fun removeLastTag() {
        val list = tags.subList(0, tags.size - 1)
        tags = list
    }
}

private inline fun taggableValueChange(
    crossinline onTag: (tagText: String) -> Unit,
    crossinline onValueChange: (TextFieldValue) -> Unit,
): (TextFieldValue) -> Unit = { it ->
    val text = it.text
    val textFieldValue = if (text.isEscaping()) {
        val taggableText = text.removeEscapingCharacters()
        if (taggableText.isNotEmpty()) {
            onTag(taggableText)
        }
        TextFieldValue()
    } else {
        it
    }
    onValueChange(textFieldValue)
}

private fun String.isEscaping(): Boolean {
    return hasSpace() || hasNewLine()
}

private fun String.removeEscapingCharacters(): String {
    return replace(" ", "").replace("\n", "")
}

private fun String.hasNewLine(): Boolean {
    return indexOf('\n') != -1
}

private fun String.hasSpace(): Boolean {
    return indexOf(' ') != -1
}

