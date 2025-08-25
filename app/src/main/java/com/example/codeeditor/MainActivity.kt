package com.example.codeeditor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeeditor.ui.theme.CodeEditorTheme
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.colorResource
import com.example.codeeditor.getDOT_ALL


private fun Unit.getDOT_ALL(): Any {
    return TODO("Provide the return value")
}


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var fileManager: FileManager
    private var currentFileName by mutableStateOf("Untitled")
    private val editorState = TextEditorState()

    override fun onPause() {
        super.onPause()
        saveFile(currentFileName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val clipboardManager = LocalClipboardManager.current
            var syntaxRules by remember { mutableStateOf(loadSyntaxRules(this, getSyntaxRulesFileName(currentFileName))) }
            var showMiniToolbar by remember { mutableStateOf(false) }
            var showFindReplace by remember { mutableStateOf(false) }
            var showCompilerInterface by remember { mutableStateOf(false) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            var compileOutput by remember { mutableStateOf("") }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            // Auto-save and commit changes
            LaunchedEffect(editorState.textField.value) {
                snapshotFlow { editorState.textField.value }
                    .debounce(500)
                    .collect {
                        editorState.commitChange()
                        saveFile(currentFileName)
                    }
            }

            LaunchedEffect(currentFileName) {
                syntaxRules = loadSyntaxRules(context, getSyntaxRulesFileName(currentFileName))
            }

            fileManager = FileManager(context)

            CodeEditorTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            initialFileName = currentFileName,
                            context = this,
                            fileManager = fileManager,
                            onNewFile = { createNewFile(it) },
                            onOpenFile = { openFile(it) },
                            onSaveFile = { saveFile(it) }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "MAD Code Editor $currentFileName",
                                        color = colorResource(id = R.color.text_primary),
                                        fontSize = 20.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = colorResource(id = R.color.text_primary)
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { showMiniToolbar = !showMiniToolbar }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = colorResource(id = R.color.secondary)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.smallTopAppBarColors(
                                    containerColor = colorResource(id = R.color.primary),
                                    titleContentColor = colorResource(id = R.color.text_primary),
                                    actionIconContentColor = colorResource(id = R.color.secondary)
                                )
                            )
                        },

                        bottomBar = {
                            BottomAppBar(
                                containerColor = colorResource(id = R.color.primary_variant),
                                actions = {
                                    Text("Chars: ${editorState.charCount.value}", color = colorResource(id = R.color.text_primary), modifier = Modifier.padding(horizontal = 8.dp))
                                    Text("Words: ${editorState.wordCount.value}", color = colorResource(id = R.color.text_primary), modifier = Modifier.padding(horizontal = 8.dp))

                                    IconButton(onClick = { editorState.undo() }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.undo),
                                            contentDescription = "Undo",
                                            tint = colorResource(id = R.color.text_primary)
                                        )
                                    }
                                    IconButton(onClick = { editorState.redo() }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.redo),
                                            contentDescription = "Redo",
                                            tint = colorResource(id = R.color.text_primary)
                                        )
                                    }
                                    IconButton(onClick = { showFindReplace = !showFindReplace }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Find",
                                            tint = colorResource(id = R.color.secondary)
                                        )
                                    }
                                    IconButton(onClick = {
                                        compileCode(
                                            context,
                                            editorState.textField.value.text,
                                            fileManager,
                                            currentFileName
                                        ) { output ->
                                            compileOutput = output
                                            showCompilerInterface = true
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBox,
                                            contentDescription = "Compile",
                                            tint = colorResource(id = R.color.secondary)
                                        )
                                    }
                                }
                            )
                        }

                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {

                            if (showCompilerInterface) {
                                CompilerInterface(
                                    clipboardManager,
                                    compileOutput, // Pass the compileOutput (which now contains instructions)
                                    onClose = { showCompilerInterface = false }
                                )
                            }

                            if (showFindReplace) {
                                FindReplaceBar(editorState = editorState, onClose = {
                                    showFindReplace = false
                                })
                            }

                            if (showMiniToolbar) {
                                MiniToolbar(
                                    onCut = { cutText(editorState.textField.value, { editorState.onTextChange(it) }, clipboardManager) },
                                    onCopy = { copyText(editorState.textField.value, clipboardManager) },
                                    onPaste = { pasteText(editorState.textField.value, { editorState.onTextChange(it) }, clipboardManager) }
                                )
                            }

                            CodeEditor(
                                modifier = Modifier.weight(1f),
                                editorState = editorState,
                                syntaxRules = syntaxRules
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createNewFile(filename: String) {
        val file = fileManager.createNewFile(filename)
        currentFileName = file
        editorState.textField.value = TextFieldValue("")
    }

    private fun saveFile(filename: String) {
        fileManager.saveFile(filename, editorState.textField.value.text)
    }

    private fun openFile(filename: String) {
        val content = fileManager.openFile(filename)
        editorState.textField.value = TextFieldValue(content)
        currentFileName = filename
    }

    private fun getSyntaxRulesFileName(fileName: String): String {
        return when (fileName.substringAfterLast('.')) {
            "kt" -> "kotlin.json"
            "java" -> "java.json"
            "py" -> "python.json"
            else -> "kotlin.json" // Default to Kotlin for unknown extensions
        }
    }
}


// CodeEditor Composable with line numbers, scrolling, and syntax highlighting
@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    editorState: TextEditorState,
    syntaxRules: SyntaxRules
) {
    val scrollState = rememberScrollState()
    val editorText = editorState.textField.value
    val lines = editorText.text.split("\n").ifEmpty { listOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {
        Row {
            // Line numbers
            Column(
                modifier = Modifier
                    .width(50.dp)
                    .padding(end = 4.dp)
            ) {
                lines.forEachIndexed { i, _ ->
                    Text(
                        text = "${i + 1}.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.text_secondary)
                        ),
                        modifier = Modifier
                            .height(24.dp)
                            .padding(vertical = 2.dp)
                    )
                }
            }

            // Editor area
            BasicTextField(
                value = editorText,
                onValueChange = { editorState.onTextChange(it) },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Transparent, // hide default text
                    lineHeight = 24.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                decorationBox = { innerTextField ->
                    Box {
                        // Highlighted text
                        Text(
                            text = highlightSyntax(editorText.text, syntaxRules),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                        )

                        // Placeholder
                        if (editorText.text.isEmpty()) {
                            Text(
                                "Type here...",
                                color = colorResource(id = R.color.text_secondary),
                                lineHeight = 24.sp
                            )
                        }

                        innerTextField() // draws cursor & input
                    }
                }
            )
        }
    }
}


// Syntax highlighting using XML colors
@Composable
fun highlightSyntax(text: String, rules: SyntaxRules): AnnotatedString {
    val keywordColor = colorResource(id = R.color.syntax_keyword)
    val commentColor = colorResource(id = R.color.syntax_comment)
    val stringColor = colorResource(id = R.color.syntax_string)
    val numberColor = colorResource(id = R.color.syntax_number)
    val operatorColor = colorResource(id = R.color.syntax_keyword) // Using keyword color for operators for now
    val typeColor = colorResource(id = R.color.syntax_string) // Using string color for types for now
    val annotationColor = colorResource(id = R.color.LightPinkAccent) // Custom color for annotations

    return buildAnnotatedString {
        append(text)

        // Keywords
        rules.keywords.forEach { keyword ->
            "\\b$keyword\\b".toRegex(RegexOption.MULTILINE).findAll(text).forEach { match ->
                addStyle(
                    SpanStyle(color = keywordColor),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }

        // Comments
        rules.comments.forEach { comment ->
            if (comment == "//") {
                Regex("//.*", RegexOption.MULTILINE).findAll(text).forEach { match ->
                    addStyle(
                        SpanStyle(color = commentColor),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            } else if (comment == "/*") {
                Regex("/\\*.*?\\*/", setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
                    .findAll(text).forEach { match ->
                    addStyle(
                        SpanStyle(color = commentColor),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }
        }

        // Strings
        val stringRegex = Regex("(\"\".*?\"\")|('.*?')", RegexOption.MULTILINE)
        stringRegex.findAll(text).forEach { match ->
            addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }

        // Numbers
        val numberRegex = "\\b\\d+(\\.\\d+)?(f|F|l|L)?\\b".toRegex()
        numberRegex.findAll(text).forEach { match ->
            addStyle(
                SpanStyle(color = numberColor),
                match.range.first,
                match.range.last + 1
            )
        }

        // Operators
        rules.operators.forEach { operator ->
            Regex("${Regex.escape(operator)}")
                .findAll(text)
                .forEach { match ->
                addStyle(
                    SpanStyle(color = operatorColor),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }

        // Types (simple regex for now, can be improved)
        rules.types.forEach { type ->
            "\\b$type\\b".toRegex().findAll(text).forEach { match ->
                addStyle(
                    SpanStyle(color = typeColor),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }

        // Annotations
        rules.annotations.forEach { annotation ->
            Regex("@${Regex.escape(annotation)}\\b").findAll(text).forEach { match ->
                addStyle(
                    SpanStyle(color = annotationColor),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
    }
}
