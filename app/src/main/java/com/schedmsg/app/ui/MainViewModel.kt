package com.schedmsg.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.schedmsg.app.data.MessageRepository
import com.schedmsg.app.data.ScheduledMessage
import com.schedmsg.app.util.ActiveMessageResolver
import com.schedmsg.app.util.AlarmScheduler
import com.schedmsg.app.widget.ActiveMessageWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class MainUiState(
    val now: LocalDateTime = LocalDateTime.now(),
    val messages: List<ScheduledMessage> = emptyList(),
    val activeMessage: ScheduledMessage? = null,
    val activeEndsAtMinutes: Int? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MessageRepository.getInstance(application)

    private val tickerFlow = MutableStateFlow(LocalDateTime.now())

    val uiState: StateFlow<MainUiState> = combine(
        repository.observeAll(),
        tickerFlow
    ) { messages, now ->
        val result = ActiveMessageResolver.findActive(messages, now)
        MainUiState(
            now = now,
            messages = messages,
            activeMessage = result.active,
            activeEndsAtMinutes = result.activeEndsAtMinutes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    init {
        // نحدّث الساعة المعروضة كل 30 ثانية فقط (وليس كل ثانية) لتوفير البطارية
        // بما أن دقة العرض المطلوبة هي بالدقيقة، وليست بالثانية.
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                tickerFlow.value = LocalDateTime.now()
            }
        }
    }

    fun saveMessage(
        id: Long?,
        text: String,
        days: List<Int>,
        startMinutes: Int,
        endMinutes: Int
    ) {
        viewModelScope.launch {
            val message = ScheduledMessage(
                id = id ?: 0,
                text = text,
                days = ScheduledMessage.daysToString(days),
                startMinutes = startMinutes,
                endMinutes = endMinutes,
                isEnabled = true
            )
            if (id == null) {
                repository.save(message)
            } else {
                repository.update(message)
            }
            refreshSchedulingAndWidget()
        }
    }

    fun deleteMessage(message: ScheduledMessage) {
        viewModelScope.launch {
            repository.delete(message)
            refreshSchedulingAndWidget()
        }
    }

    fun toggleEnabled(message: ScheduledMessage) {
        viewModelScope.launch {
            repository.update(message.copy(isEnabled = !message.isEnabled))
            refreshSchedulingAndWidget()
        }
    }

    private suspend fun refreshSchedulingAndWidget() {
        val all = repository.getAllOnce()
        val app = getApplication<Application>()
        AlarmScheduler.rescheduleAll(app, all)
        ActiveMessageWidget.updateAll(app)
        tickerFlow.value = LocalDateTime.now()
    }
}
