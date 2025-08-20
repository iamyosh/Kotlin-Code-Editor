package com.example.codeeditor

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

class TextEditorState(initialText: TextFieldValue = TextFieldValue("")) {
    var textField = mutableStateOf(initialText)
        private set

    private val undoStack = ArrayDeque<TextFieldValue>()
    private val redoStack = ArrayDeque<TextFieldValue>()
    private var lastCommittedText = initialText

    // Called on every text chang
    fun onTextChange(newValue: TextFieldValue) {
        textField.value = newValue
    }

    fun commitChange() {
        if (textField.value.text != lastCommittedText.text) {
            undoStack.addLast(lastCommittedText)
            redoStack.clear()
            lastCommittedText = textField.value
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.addLast(textField.value)
            textField.value = undoStack.removeLast()
            lastCommittedText = textField.value
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.addLast(textField.value)
            textField.value = redoStack.removeLast()
            lastCommittedText = textField.value
        }
    }



    fun replace(find: String, replace: String) {
        Log.d("FileManager", "Saved to ${find}  ${replace}")
        val text = textField.value.text
        val index = text.indexOf(find, ignoreCase = true)
        if (index >= 0) {
            val newText = text.replaceFirst(find, replace, ignoreCase = true)
            onTextChange(TextFieldValue(newText))
            commitChange()
        }
    }

    fun replaceAll(find: String, replace: String) {
        val text = textField.value.text
        if (text.contains(find, ignoreCase = true)) {
            val newText = text.replace(find, replace, ignoreCase = true)
            onTextChange(TextFieldValue(newText))
            commitChange()
        }
    }
}
