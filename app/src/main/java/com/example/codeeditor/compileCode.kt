package com.example.codeeditor

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

fun compileCode(
    context: Context,
    code: String,
    fileManager: FileManager,
    fileName: String,
    onResult: (String) -> Unit
) {
    // 1. Save the Kotlin file internally
    fileManager.saveFile(fileName, code)
    val internalFile = File(context.filesDir, fileName)
    val externalDir = context.getExternalFilesDir(null) // app-specific external folder
    val externalFile = File(externalDir, fileName)

    if (internalFile.exists()) {
        internalFile.copyTo(externalFile, overwrite = true)
    }

    // 2. Generate instructions for the user to compile on their PC via ADB
    val adbPushCommand = "adb push \"${externalFile.absolutePath}\" /sdcard/"
    val compileCommand = "kotlin-compiler /sdcard/$fileName"

    val instructions = """
Kotlin file saved to: ${externalFile.absolutePath}

To compile on your desktop machine:
1. Open a terminal on your desktop.
2. Push the file to your Android device (if not already there):
   $adbPushCommand
3. Compile it using your Kotlin compiler (adjust command if necessary):
   $compileCommand
4. Copy the output from your desktop compiler and paste it into the 'Compiler Output' field below.
""".trimIndent()

    onResult(instructions)
}
