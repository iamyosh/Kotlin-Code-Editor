package com.example.codeeditor

import android.content.Context
import android.util.Log
import java.io.File

class FileManager(private val context: Context) {

    fun createNewFile(fileName: String ): String {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.name

    }

    fun saveFile(fileName: String, content: String) {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(content)
        Log.d("FileManager", "Saved to ${file.absolutePath}")

    }

    fun openFile(fileName: String): String {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file.readText() else ""
    }
}