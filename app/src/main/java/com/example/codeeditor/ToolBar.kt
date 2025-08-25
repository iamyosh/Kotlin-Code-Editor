package com.example.codeeditor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.codeeditor.ui.theme.BlackPrimary
import com.example.codeeditor.ui.theme.LightPinkAccent
import com.example.codeeditor.ui.theme.WhiteText
import androidx.compose.material3.ButtonDefaults

/**
 * MiniToolbar Composable
 *
 * A simple toolbar UI that provides buttons for text editing actions:
 * - Cut
 * - Copy
 * - Paste
 *
 * The actual behavior is passed in through the callbacks [onCut], [onCopy], and [onPaste].
 */
@Composable
fun MiniToolbar(
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit
) {
    Log.d("FileManager", "Mini tool bar")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackPrimary) // Themed background
            .padding(8.dp),
    ) {
        TextButton(onClick = onCut, colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent)) { Text("Cut", color = WhiteText) }
        TextButton(onClick = onCopy, colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent)) { Text("Copy", color = WhiteText) }
        TextButton(onClick = onPaste, colors = ButtonDefaults.textButtonColors(contentColor = LightPinkAccent)) { Text("Paste", color = WhiteText) }
    }
}