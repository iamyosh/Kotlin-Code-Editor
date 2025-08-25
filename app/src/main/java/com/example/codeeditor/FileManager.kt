package com.example.codeeditor

import android.content.Context
import android.util.Log
import java.io.File

class FileManager(private val context: Context) {

    // Create a new file (if not exists) and return its name
    fun createNewFile(fileName: String): String {
        val file = File(context.filesDir, ensureFileExtension(fileName))
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.name
    }

    // Save text content to a file (creates it if missing)
    fun saveFile(fileName: String, content: String) {
        val file = File(context.filesDir, ensureFileExtension(fileName))
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(content)
        Log.d("FileManager", "Saved to ${file.absolutePath}")
    }

    // Open a file and return its content (empty if not exists)
    fun openFile(fileName: String): String {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file.readText() else ""
    }

    // List all files in the app's internal storage directory
    fun listFiles(): List<String> {
        return context.filesDir.listFiles()?.map { it.name } ?: emptyList()
    }

    // Delete a file
    fun deleteFile(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.delete()
    }

    // Ensure file has an extension, defaults to .kt
    private fun ensureFileExtension(fileName: String): String {
        return if (fileName.contains(".")) fileName else "$fileName.kt"
    }
}
