package com.meetingminutes.app.data.dao

import androidx.room.*
import com.meetingminutes.app.data.model.MeetingRecord
import kotlinx.coroutines.flow.Flow

/**
 * 会议记录数据访问对象
 */
@Dao
interface MeetingRecordDao {
    
    @Query("SELECT * FROM meeting_records ORDER BY createTime DESC")
    fun getAllRecords(): Flow<List<MeetingRecord>>
    
    @Query("SELECT * FROM meeting_records WHERE id = :id")
    suspend fun getRecordById(id: Long): MeetingRecord?
    
    @Query("SELECT * FROM meeting_records WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun searchRecords(keyword: String): Flow<List<MeetingRecord>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: MeetingRecord): Long
    
    @Update
    suspend fun update(record: MeetingRecord)
    
    @Delete
    suspend fun delete(record: MeetingRecord)
    
    @Query("DELETE FROM meeting_records WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT COUNT(*) FROM meeting_records")
    suspend fun getRecordCount(): Int
}

