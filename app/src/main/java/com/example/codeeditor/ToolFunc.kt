package com.example.codeeditor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.TextRange

fun cutText(
    editorText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    clipboardManager: ClipboardManager
) {
    val start = editorText.selection.start
    val end = editorText.selection.end
    if (start == end) return // nothing selected

    val selectedText = editorText.text.substring(start, end)
    clipboardManager.setText(AnnotatedString(selectedText))

    val newText = editorText.text.removeRange(start, end)
    onTextChange(editorText.copy(text = newText, selection = TextRange(start)))
}

fun copyText(
    editorText: TextFieldValue,
    clipboardManager: ClipboardManager
) {
    val start = editorText.selection.start
    val end = editorText.selection.end
    if (start == end) return // nothing selected

    val selectedText = editorText.text.substring(start, end)
    clipboardManager.setText(AnnotatedString(selectedText))
}

fun pasteText(
    editorText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    clipboardManager: ClipboardManager
) {
    val clipText = clipboardManager.getText()?.text ?: ""
    val start = editorText.selection.start
    val end = editorText.selection.end

    val newText = editorText.text.replaceRange(start, end, clipText)
    onTextChange(editorText.copy(text = newText, selection = TextRange(start + clipText.length)))
}

