package com.example.codeeditor

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.codeeditor.ui.theme.DarkPinkAccent
import com.example.codeeditor.ui.theme.ErrorRed
import com.example.codeeditor.ui.theme.GreyBackground
import com.example.codeeditor.ui.theme.GreyText
import com.example.codeeditor.ui.theme.LightPinkAccent
import com.example.codeeditor.ui.theme.SuccessGreen
import com.example.codeeditor.ui.theme.WhiteText
import androidx.compose.ui.text.TextStyle

@Composable
fun CompilerInterface(
    clipboardManager: ClipboardManager,
    initialInstructions: String,
    onClose: () -> Unit
) {
    var compileOutput by remember { mutableStateOf("") }
    var compilerStatus by remember { mutableStateOf("Waiting for output...") }
    var statusColor by remember { mutableStateOf(GreyText) }

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text("Compiler Result", color = WhiteText) }, // Themed title
        containerColor = GreyBackground, // Themed background
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(
                    text = initialInstructions,
                    color = WhiteText, // Themed text
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = compileOutput,
                    onValueChange = {
                        compileOutput = it
                        // Simple parsing for success/failure. Can be made more robust.
                        compilerStatus = when {
                            it.contains("error:", ignoreCase = true) -> {
                                statusColor = ErrorRed
                                "Compilation Failed"
                            }
                            it.contains("success", ignoreCase = true) || it.contains("compiled", ignoreCase = true) -> {
                                statusColor = SuccessGreen
                                "Compilation Successful"
                            }
                            else -> {
                                statusColor = GreyText
                                "Waiting for output..."
                            }
                        }
                    },
                    label = { Text("Paste Compiler Output Here", color = GreyText) }, // Themed label
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    textStyle = TextStyle(color = WhiteText) // Themed text
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Status: $compilerStatus",
                    color = statusColor, // Themed status color
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row {
                Button(
                    onClick = {
                        if (compileOutput.isNotEmpty()) {
                            clipboardManager.setText(AnnotatedString(compileOutput))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPinkAccent, contentColor = WhiteText) // Themed button
                ) {
                    Text("Copy Output")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onClose() },
                    colors = ButtonDefaults.buttonColors(containerColor = LightPinkAccent, contentColor = WhiteText) // Themed button
                ) {
                    Text("OK")
                }
            }
        }
    )
}