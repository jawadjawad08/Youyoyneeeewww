package com.schedmsg.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledMessageDao {

    @Query("SELECT * FROM scheduled_messages ORDER BY startMinutes ASC")
    fun observeAll(): Flow<List<ScheduledMessage>>

    @Query("SELECT * FROM scheduled_messages ORDER BY startMinutes ASC")
    suspend fun getAllOnce(): List<ScheduledMessage>

    @Query("SELECT * FROM scheduled_messages WHERE id = :id")
    suspend fun getById(id: Long): ScheduledMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(message: ScheduledMessage): Long

    @Update
    suspend fun update(message: ScheduledMessage)

    @Delete
    suspend fun delete(message: ScheduledMessage)

    @Query("DELETE FROM scheduled_messages WHERE id = :id")
    suspend fun deleteById(id: Long)
}
