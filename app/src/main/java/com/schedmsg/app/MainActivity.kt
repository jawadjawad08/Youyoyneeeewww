package com.schedmsg.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schedmsg.app.data.ScheduledMessage
import com.schedmsg.app.ui.MainViewModel
import com.schedmsg.app.ui.screens.AddEditMessageScreen
import com.schedmsg.app.ui.screens.HomeScreen
import com.schedmsg.app.ui.screens.IconPickerScreen
import com.schedmsg.app.ui.theme.ScheduledMessagesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScheduledMessagesTheme {
                AppRoot()
            }
        }
    }
}

private sealed class Screen {
    data object Home : Screen()
    data class AddEdit(val existing: ScheduledMessage?) : Screen()
    data object IconPicker : Screen()
}

@Composable
private fun AppRoot() {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    when (val current = screen) {
        is Screen.Home -> {
            HomeScreen(
                state = state,
                onAddClick = { screen = Screen.AddEdit(null) },
                onMessageClick = { msg -> screen = Screen.AddEdit(msg) },
                onToggleEnabled = { msg -> viewModel.toggleEnabled(msg) },
                onIconPickerClick = { screen = Screen.IconPicker }
            )
        }
        is Screen.AddEdit -> {
            AddEditMessageScreen(
                existing = current.existing,
                onSave = { id, text, days, start, end ->
                    viewModel.saveMessage(id, text, days, start, end)
                    screen = Screen.Home
                },
                onDelete = { msg ->
                    viewModel.deleteMessage(msg)
                    screen = Screen.Home
                },
                onBack = { screen = Screen.Home }
            )
        }
        is Screen.IconPicker -> {
            IconPickerScreen(onBack = { screen = Screen.Home })
        }
    }
}
