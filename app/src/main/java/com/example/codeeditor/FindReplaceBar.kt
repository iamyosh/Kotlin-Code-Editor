package com.example.codeeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.codeeditor.ui.theme.DarkPinkAccent
import com.example.codeeditor.ui.theme.GreyBackground
import com.example.codeeditor.ui.theme.GreyText
import com.example.codeeditor.ui.theme.LightPinkAccent
import com.example.codeeditor.ui.theme.WhiteText
import androidx.compose.ui.text.TextStyle

/**
 * FindReplaceBar Composable
 *
 * A dialog UI that allows the user to:
 * - Enter a search term (Find).
 * - Enter replacement text.
 * - Replace the first occurrence or all occurrences in the editor.
 *
 * Uses [TextEditorState] for performing replace operations.
 * [onClose] is called when the dialog is dismissed or after an action.
 */

@Composable
fun FindReplaceBar(
    editorState: TextEditorState,
    onClose: () -> Unit
) {
    var findText = remember { mutableStateOf("") }
    var replaceText = remember { mutableStateOf("") }
    var matchCase = remember { mutableStateOf(false) }
    var wholeWord = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text("Find and Replace", color = WhiteText) }, // Themed title
        containerColor = GreyBackground, // Themed background
        text = {
            Column {
                OutlinedTextField(
                    value = findText.value,
                    onValueChange = { findText.value = it },
                    label = { Text("Find", color = GreyText) }, // Themed label
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = WhiteText) // Themed text
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replaceText.value,
                    onValueChange = { replaceText.value = it },
                    label = { Text("Replace", color = GreyText) }, // Themed label
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = WhiteText) // Themed text
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = matchCase.value,
                        onCheckedChange = { matchCase.value = it },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = DarkPinkAccent, uncheckedColor = GreyText) // Themed checkbox
                    )
                    Text("Match Case", color = WhiteText) // Themed text
                    Checkbox(
                        checked = wholeWord.value,
                        onCheckedChange = { wholeWord.value = it },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = DarkPinkAccent, uncheckedColor = GreyText) // Themed checkbox
                    )
                    Text("Whole Word", color = WhiteText) // Themed text
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    editorState.replace(findText.value, replaceText.value, matchCase.value, wholeWord.value)
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPinkAccent, contentColor = WhiteText) // Themed button
            ) {
                Text("Replace")
            }
            Button(
                onClick = {
                    editorState.replaceAll(findText.value, replaceText.value, matchCase.value, wholeWord.value)
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LightPinkAccent, contentColor = WhiteText) // Themed button
            ) {
                Text("Replace All")
            }
        }

    )
}

