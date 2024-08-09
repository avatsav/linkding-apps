package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class PlaceholderVisualTransformation(
    private val text: String,
    private val color: Color,
) : VisualTransformation {

    private val placeHolderTransformedText = TransformedText(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = color)) {
                append(text)
            }
        },
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = 0
            override fun transformedToOriginal(offset: Int): Int = 0
        },
    )

    override fun filter(text: AnnotatedString): TransformedText =
        if (text.isEmpty() && this.text.isNotBlank()) {
            placeHolderTransformedText
        } else {
            TransformedText(text, OffsetMapping.Identity)
        }
}
