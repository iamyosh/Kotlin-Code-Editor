package com.example.codeeditor

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.text.Regex
import kotlin.text.RegexOption


class TextEditorState(initialText: TextFieldValue = TextFieldValue("")) {
    // Current text state
    var textField = mutableStateOf(initialText)
        private set

    var charCount = mutableStateOf(initialText.text.length)
        private set
    var wordCount = mutableStateOf(countWords(initialText.text))
        private set

    private val undoStack = ArrayDeque<TextFieldValue>() // history for undo
    private val redoStack = ArrayDeque<TextFieldValue>() // history for redo
    private var lastCommittedText = initialText

    // Update text when user types
    fun onTextChange(newValue: TextFieldValue) {
        textField.value = newValue
        charCount.value = newValue.text.length
        wordCount.value = countWords(newValue.text)
    }

    private fun countWords(text: String): Int {
        if (text.isEmpty()) return 0
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        return words.size
    }

    // Save current change to history
    fun commitChange() {
        if (textField.value.text != lastCommittedText.text) {
            undoStack.addLast(lastCommittedText)
            redoStack.clear()
            lastCommittedText = textField.value
        }
    }

    // Undo last change
    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.addLast(textField.value)
            textField.value = undoStack.removeLast()
            lastCommittedText = textField.value
        }
    }

    // Redo undone change
    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.addLast(textField.value)
            textField.value = redoStack.removeLast()
            lastCommittedText = textField.value
        }
    }

    // Replace first match
    fun replace(find: String, replace: String, matchCase: Boolean, wholeWord: Boolean) {
        val text = textField.value.text
        val findRegex = if (wholeWord) "\\b${Regex.escape(find)}\\b" else Regex.escape(find)
        val options = if (matchCase) setOf<RegexOption>() else setOf(RegexOption.IGNORE_CASE)
        val regex = Regex(findRegex, options)

        val match = regex.find(text)
        if (match != null) {
            val newText = text.replaceRange(match.range, replace)
            onTextChange(TextFieldValue(newText))
            commitChange()
        }
    }

    // Replace all matches
    fun replaceAll(find: String, replace: String, matchCase: Boolean, wholeWord: Boolean) {
        val text = textField.value.text
        val findRegex = if (wholeWord) "\\b${Regex.escape(find)}\\b" else Regex.escape(find)
        val options = if (matchCase) setOf<RegexOption>() else setOf(RegexOption.IGNORE_CASE)
        val regex = Regex(findRegex, options)

        val newText = text.replace(regex, replace)
        if (newText != text) {
            onTextChange(TextFieldValue(newText))
            commitChange()
        }
    }
}

