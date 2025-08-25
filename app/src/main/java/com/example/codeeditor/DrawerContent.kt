package com.example.codeeditor

import android.R.attr.text
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeeditor.ui.theme.BlackPrimary
import com.example.codeeditor.ui.theme.DarkPinkAccent
import com.example.codeeditor.ui.theme.GreyBackground
import com.example.codeeditor.ui.theme.GreyText
import com.example.codeeditor.ui.theme.LightPinkAccent
import com.example.codeeditor.ui.theme.WhiteText

// Composable drawer UI with options to create, open, and save files
@Composable
fun DrawerContent(
    initialFileName: String,
    context: Context,
    fileManager: FileManager,
    onNewFile: (String) -> Unit,
    onOpenFile: (String) -> Unit,
    onSaveFile: (String) -> Unit
) {
    var fileName = remember { mutableStateOf(initialFileName) }
    var showDialog = remember { mutableStateOf(false) }
    var showSaveDialog = remember { mutableStateOf(false) }
    var showOpenDialog = remember { mutableStateOf(false) }
    var files = remember { mutableStateOf(fileManager.listFiles()) }
    val extensions = listOf(".kt", ".txt", ".java")
    var selectedExtension = remember { mutableStateOf(extensions.first()) }

    LaunchedEffect(showOpenDialog.value) {
        if (showOpenDialog.value) {
            files.value = fileManager.listFiles()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.6f)
            .background(BlackPrimary) // Themed background
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        TextButton(
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent) // Themed button
        ) {
            Text("New File", fontSize = 18.sp)
        }

        TextButton(
            onClick = { showOpenDialog.value = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent) // Themed button
        ) {
            Text("Open", fontSize = 18.sp)
        }

        TextButton(
            onClick = { showSaveDialog.value = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent) // Themed button
        ) {
            Text("Save", fontSize = 18.sp)
        }
    }

    // Show "New File" dialog
    if (showDialog.value) {

        var expanded = remember { mutableStateOf(false) }


        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            containerColor = GreyBackground, // Themed dialog background
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalName = if (fileName.value.endsWith(selectedExtension.value)) {
                            fileName.value
                        } else {
                            fileName.value + selectedExtension.value
                        }
                        onNewFile(finalName)
                        showDialog.value = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent) // Themed button
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = GreyText) // Themed button
                ) { Text("Cancel") }
            },
            title = { Text("Create New File", color = WhiteText) }, // Themed text
            text = {
                Column {
                    OutlinedTextField(
                        value = fileName.value,
                        onValueChange = { fileName.value = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        trailingIcon = { Text(selectedExtension.value, color = GreyText) }, // Themed text
                        textStyle = androidx.compose.ui.text.TextStyle(color = WhiteText) // Themed text
                    )
                    Button(
                        onClick = { expanded.value = !expanded.value },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPinkAccent, contentColor = WhiteText) // Themed button
                    ) {
                        Text("Select extension")
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Arrow Down",
                            tint = WhiteText // Themed icon
                        )
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.background(GreyBackground) // Themed dropdown
                    ) {
                        extensions.forEach { ext ->
                            DropdownMenuItem(
                                text = { Text(ext, color = WhiteText) }, // Themed text
                                onClick = {
                                    selectedExtension.value = ext
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }


            }
        )
    }

    // Show "Save File" dialog
    if (showSaveDialog.value) {
        AlertDialog(
            onDismissRequest = { showSaveDialog.value = false },
            containerColor = GreyBackground, // Themed dialog background
            confirmButton = {
                TextButton(
                    onClick = {
                        onSaveFile(fileName.value)
                        Toast.makeText(context, "File Saved!", Toast.LENGTH_SHORT).show()
                        showSaveDialog.value = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent) // Themed button
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog.value = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = GreyText) // Themed button
                ) { Text("Cancel") }
            },
            title = { Text("Save the file", color = WhiteText) } // Themed text
        )
    }
    // Show "Open File" dialog
    if (showOpenDialog.value) {
        AlertDialog(
            onDismissRequest = { showOpenDialog.value = false },
            title = { Text("Select a file", color = WhiteText) }, // Themed text
            containerColor = GreyBackground, // Themed dialog background
            text = {
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(files.value.size) { index ->
                        val file = files.value[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenFile(file)
                                    showOpenDialog.value = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = file,
                                color = WhiteText, // Themed text
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (fileManager.deleteFile(file)) {
                                    files.value = fileManager.listFiles() // Refresh the list
                                    Toast.makeText(context, "File deleted: $file", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to delete: $file", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = LightPinkAccent) // Themed icon
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showOpenDialog.value = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = GreyText) // Themed button
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
