package com.meetingminutes.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 会议记录实体
 */
@Entity(tableName = "meeting_records")
data class MeetingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 会议标题
    val title: String,
    
    // 录音文件路径
    val audioPath: String? = null,
    
    // 转换后的文字内容
    val content: String = "",
    
    // AI 总结内容
    val summary: String = "",
    
    // 创建时间
    val createTime: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updateTime: Long = System.currentTimeMillis(),
    
    // 录音时长（秒）
    val duration: Int = 0,
    
    // 录音状态：0-录音中，1-已完成，2-已导入
    val status: Int = 0,
    
    // 使用的方言类型
    val dialect: String = "mandarin",
    
    // 是否已生成 AI 总结
    val hasSummary: Boolean = false,
    
    // 标签
    val tags: String = ""
) {
    fun getFormattedDate(): String {
        val date = Date(createTime)
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", date).toString()
    }
    
    fun getFormattedDuration(): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
}

