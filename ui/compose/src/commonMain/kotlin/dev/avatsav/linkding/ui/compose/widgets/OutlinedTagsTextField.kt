package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.ui.compose.onCondition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTagsTextField(
  value: TagsTextFieldValue,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  var textValue by remember { mutableStateOf(TextFieldValue()) }

  Box(modifier = modifier.onCondition(label != null) { padding(top = 4.dp) }) {
    OutlinedTagsTextField(
      tagsValue = value,
      textValue = textValue,
      onTextValueChange = { textValue = it },
      keyboardOptions = keyboardOptions,
      interactionSource = interactionSource,
      decorationBox = { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
          value = if (value.tags.isEmpty() && textValue.text.isEmpty()) "" else " ",
          innerTextField = innerTextField,
          enabled = true,
          singleLine = false,
          visualTransformation = VisualTransformation.None,
          interactionSource = interactionSource,
          isError = false,
          label = label,
          supportingText = supportingText,
          colors = OutlinedTextFieldDefaults.colors(),
          contentPadding = OutlinedTextFieldDefaults.contentPadding(),
          container = {
            Container(
              enabled = true,
              isError = false,
              interactionSource = interactionSource,
              colors = OutlinedTextFieldDefaults.colors(),
            )
          },
        )
      },
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OutlinedTagsTextField(
  tagsValue: TagsTextFieldValue,
  textValue: TextFieldValue,
  onTextValueChange: (TextFieldValue) -> Unit,
  modifier: Modifier = Modifier,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  textStyle: TextStyle = LocalTextStyle.current,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
    @Composable { innerTextField -> innerTextField() },
) {
  val textFieldFocusRequester = remember { FocusRequester() }

  decorationBox {
    FlowRow(
      modifier =
        modifier.fillMaxWidth().pointerInput(textValue) {
          detectTapGestures(onTap = { textFieldFocusRequester.requestFocus() })
        },
      horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
      verticalArrangement = Arrangement.spacedBy((-3).dp, Alignment.Top),
    ) {
      repeat(tagsValue.tags.size) {
        val tag = tagsValue.tags[it]
        TagInputChip(label = { Text(tag.value) }, onClick = { tagsValue.removeTag(tag) })
      }
      BasicTextField(
        value = textValue,
        modifier =
          Modifier.focusRequester(textFieldFocusRequester)
            .align(alignment = Alignment.CenterVertically)
            .onPreviewKeyEvent { keyEvent ->
              if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                if (textValue.text.isEmpty() && tagsValue.tags.isNotEmpty()) {
                  tagsValue.removeLastTag()
                  return@onPreviewKeyEvent true
                }
              }
              false
            },
        onValueChange =
          tagValueChange(
            onTag = { tagText -> tagsValue.addTag(Tag(tagText)) },
            onValueChange = onTextValueChange,
          ),
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
        keyboardActions =
          KeyboardActions(
            onDone = {
              val tagText = textValue.text
              if (tagText.isNotEmpty()) {
                tagsValue.addTag(Tag(tagText))
                onTextValueChange(TextFieldValue())
              }
            }
          ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
      )
    }
  }
}

@Stable data class Tag(val value: String)

@Stable
class TagsTextFieldValue(tags: List<Tag> = emptyList()) {

  var tags by mutableStateOf(tags)

  fun addTag(tag: Tag) {
    if (tags.contains(tag)) return
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

private inline fun tagValueChange(
  crossinline onTag: (tagText: String) -> Unit,
  crossinline onValueChange: (TextFieldValue) -> Unit,
): (TextFieldValue) -> Unit = { it ->
  val text = it.text
  val textFieldValue =
    if (text.isEscaping()) {
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

private fun String.isEscaping(): Boolean = hasSpace() || hasNewLine()

private fun String.removeEscapingCharacters(): String = replace(" ", "").replace("\n", "")

private fun String.hasNewLine(): Boolean = indexOf('\n') != -1

private fun String.hasSpace(): Boolean = indexOf(' ') != -1
