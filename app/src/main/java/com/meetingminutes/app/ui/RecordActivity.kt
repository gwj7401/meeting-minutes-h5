package com.meetingminutes.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.meetingminutes.app.MeetingApp
import com.meetingminutes.app.data.model.MeetingRecord
import com.meetingminutes.app.databinding.ActivityRecordBinding
import com.meetingminutes.app.recorder.AudioRecorder
import com.meetingminutes.app.speech.AliSpeechRecognizer
import com.meetingminutes.app.speech.DialectType
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 录音界面
 */
class RecordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRecordBinding
    private val audioRecorder = AudioRecorder()
    private val speechRecognizer = AliSpeechRecognizer()
    private val database by lazy { (application as MeetingApp).database }
    
    private var isRecording = false
    private var isPaused = false
    private var currentRecordId: Long = 0
    private var audioFilePath: String = ""
    private val recognizedText = StringBuilder()
    private var selectedDialect = DialectType.MANDARIN
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecorder()
        setupSpeechRecognizer()
        setupButtons()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupRecorder() {
        audioRecorder.onAudioData = { data, length ->
            // 发送音频数据到语音识别
            if (isRecording && !isPaused) {
                speechRecognizer.sendAudioData(data, length)
            }
        }
        
        audioRecorder.onDurationUpdate = { duration ->
            binding.tvDuration.text = formatDuration(duration)
        }
        
        audioRecorder.onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupSpeechRecognizer() {
        speechRecognizer.setOnResultListener { text, isFinal ->
            runOnUiThread {
                if (isFinal) {
                    recognizedText.append(text)
                    binding.tvRecognizedText.text = recognizedText.toString()
                } else {
                    // 显示临时结果
                    binding.tvTempResult.text = text
                }
            }
        }
        
        speechRecognizer.setOnErrorListener { error ->
            runOnUiThread {
                Toast.makeText(this, "识别错误: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupButtons() {
        // 开始/停止录音按钮
        binding.btnRecord.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }
        
        // 暂停/继续按钮
        binding.btnPause.setOnClickListener {
            if (isPaused) {
                resumeRecording()
            } else {
                pauseRecording()
            }
        }
        
        // 选择方言
        binding.btnSelectDialect.setOnClickListener {
            showDialectSelector()
        }
        
        // 保存按钮
        binding.btnSave.setOnClickListener {
            saveRecord()
        }
    }
    
    private fun startRecording() {
        // 生成音频文件路径
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val audioDir = File(getExternalFilesDir(null), "recordings")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        audioFilePath = File(audioDir, "recording_$timestamp.wav").absolutePath
        
        // 开始录音
        if (audioRecorder.startRecording(audioFilePath)) {
            isRecording = true
            isPaused = false
            
            // 开始语音识别
            speechRecognizer.startRealTimeRecognition(selectedDialect)
            
            // 更新 UI
            binding.btnRecord.text = "停止录音"
            binding.btnPause.isEnabled = true
            binding.btnSelectDialect.isEnabled = false
            binding.statusText.text = "正在录音..."
            
            Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "录音启动失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun pauseRecording() {
        audioRecorder.pauseRecording()
        isPaused = true
        
        binding.btnPause.text = "继续"
        binding.statusText.text = "已暂停"
    }
    
    private fun resumeRecording() {
        audioRecorder.resumeRecording()
        isPaused = false
        
        binding.btnPause.text = "暂停"
        binding.statusText.text = "正在录音..."
    }
    
    private fun stopRecording() {
        val audioFile = audioRecorder.stopRecording()
        speechRecognizer.stopRecognition()
        
        isRecording = false
        isPaused = false
        
        // 更新 UI
        binding.btnRecord.text = "开始录音"
        binding.btnPause.isEnabled = false
        binding.btnPause.text = "暂停"
        binding.btnSelectDialect.isEnabled = true
        binding.statusText.text = "录音已完成"
        binding.btnSave.isEnabled = true
        
        Toast.makeText(this, "录音已停止", Toast.LENGTH_SHORT).show()
    }
    
    private fun showDialectSelector() {
        val dialects = DialectType.getAllDialects()
        val items = dialects.map { it.second }.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("选择方言")
            .setSingleChoiceItems(items, dialects.indexOfFirst { it.first == selectedDialect }) { dialog, which ->
                selectedDialect = dialects[which].first
                binding.tvDialect.text = dialects[which].second
                dialog.dismiss()
            }
            .show()
    }
    
    private fun saveRecord() {
        // 创建输入框
        val editText = android.widget.EditText(this)
        editText.hint = "请输入会议标题"
        editText.setPadding(50, 30, 50, 30)

        MaterialAlertDialogBuilder(this)
            .setTitle("保存录音")
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val title = editText.text.toString().ifEmpty { "未命名会议" }

                lifecycleScope.launch {
                    val record = MeetingRecord(
                        title = title,
                        audioPath = audioFilePath,
                        content = recognizedText.toString(),
                        duration = audioRecorder.getCurrentDuration(),
                        status = 1,
                        dialect = selectedDialect
                    )

                    database.meetingRecordDao().insert(record)

                    Toast.makeText(this@RecordActivity, "已保存", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, secs)
            else -> String.format("%02d:%02d", minutes, secs)
        }
    }
    
    override fun onBackPressed() {
        if (isRecording) {
            MaterialAlertDialogBuilder(this)
                .setTitle("确认退出")
                .setMessage("录音正在进行中，确定要退出吗？")
                .setPositiveButton("退出") { _, _ ->
                    stopRecording()
                    super.onBackPressed()
                }
                .setNegativeButton("取消", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            audioRecorder.stopRecording()
        }
        speechRecognizer.release()
    }
}

