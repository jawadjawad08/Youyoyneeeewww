package com.schedmsg.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * طبقة وسيطة بسيطة بين واجهة المستخدم/الودجت وقاعدة البيانات المحلية.
 * كل البيانات تبقى على الجهاز فقط، لا يوجد أي اتصال بالإنترنت.
 */
class MessageRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).scheduledMessageDao()

    fun observeAll(): Flow<List<ScheduledMessage>> = dao.observeAll()

    suspend fun getAllOnce(): List<ScheduledMessage> = dao.getAllOnce()

    suspend fun save(message: ScheduledMessage): Long = dao.upsert(message)

    suspend fun update(message: ScheduledMessage) = dao.update(message)

    suspend fun delete(message: ScheduledMessage) = dao.delete(message)

    companion object {
        @Volatile
        private var INSTANCE: MessageRepository? = null

        fun getInstance(context: Context): MessageRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MessageRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
