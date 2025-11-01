package com.meetingminutes.app.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

/**
 * 音频录制器
 */
class AudioRecorder {
    
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var isPaused = false
    private var recordingJob: Job? = null
    private var outputFile: File? = null
    
    private val sampleRate = 16000 // 16kHz 采样率，适合语音识别
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    
    private var recordingStartTime = 0L
    private var pausedDuration = 0L
    private var lastPauseTime = 0L
    
    var onAudioData: ((ByteArray, Int) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onDurationUpdate: ((Int) -> Unit)? = null
    
    /**
     * 开始录音
     */
    fun startRecording(outputPath: String): Boolean {
        try {
            outputFile = File(outputPath)
            outputFile?.parentFile?.mkdirs()
            
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize * 2
            )
            
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                onError?.invoke("AudioRecord 初始化失败")
                return false
            }
            
            audioRecord?.startRecording()
            isRecording = true
            isPaused = false
            recordingStartTime = System.currentTimeMillis()
            pausedDuration = 0
            
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                recordAudio()
            }
            
            // 启动时长更新
            startDurationUpdate()
            
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            onError?.invoke("录音启动失败: ${e.message}")
            return false
        }
    }
    
    /**
     * 暂停录音
     */
    fun pauseRecording() {
        if (isRecording && !isPaused) {
            isPaused = true
            lastPauseTime = System.currentTimeMillis()
        }
    }
    
    /**
     * 恢复录音
     */
    fun resumeRecording() {
        if (isRecording && isPaused) {
            pausedDuration += System.currentTimeMillis() - lastPauseTime
            isPaused = false
        }
    }
    
    /**
     * 停止录音
     */
    fun stopRecording(): File? {
        isRecording = false
        isPaused = false
        
        recordingJob?.cancel()
        recordingJob = null
        
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        
        // 写入 WAV 文件头
        outputFile?.let { file ->
            if (file.exists()) {
                writeWavHeader(file)
                return file
            }
        }
        
        return null
    }
    
    /**
     * 获取当前录音时长（秒）
     */
    fun getCurrentDuration(): Int {
        if (!isRecording) return 0
        val elapsed = System.currentTimeMillis() - recordingStartTime - pausedDuration
        return (elapsed / 1000).toInt()
    }
    
    private suspend fun recordAudio() {
        val buffer = ByteArray(bufferSize)
        val fos = FileOutputStream(outputFile)
        
        // 写入占位的 WAV 头（44字节）
        fos.write(ByteArray(44))
        
        try {
            while (isRecording) {
                if (!isPaused) {
                    val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readSize > 0) {
                        fos.write(buffer, 0, readSize)
                        
                        // 回调音频数据用于实时识别
                        withContext(Dispatchers.Main) {
                            onAudioData?.invoke(buffer, readSize)
                        }
                    }
                } else {
                    delay(100)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onError?.invoke("录音过程出错: ${e.message}")
            }
        } finally {
            fos.close()
        }
    }
    
    private fun startDurationUpdate() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isRecording) {
                onDurationUpdate?.invoke(getCurrentDuration())
                delay(1000)
            }
        }
    }
    
    /**
     * 写入 WAV 文件头
     */
    private fun writeWavHeader(file: File) {
        try {
            val randomAccessFile = RandomAccessFile(file, "rw")
            val totalDataLen = randomAccessFile.length() - 44
            val totalAudioLen = totalDataLen + 36
            val channels = 1
            val byteRate = sampleRate * channels * 2
            
            randomAccessFile.seek(0)
            
            // RIFF header
            randomAccessFile.writeBytes("RIFF")
            writeIntLittleEndian(randomAccessFile, totalAudioLen.toInt())
            randomAccessFile.writeBytes("WAVE")

            // fmt chunk
            randomAccessFile.writeBytes("fmt ")
            writeIntLittleEndian(randomAccessFile, 16) // chunk size
            writeShortLittleEndian(randomAccessFile, 1) // audio format (PCM)
            writeShortLittleEndian(randomAccessFile, channels.toShort())
            writeIntLittleEndian(randomAccessFile, sampleRate)
            writeIntLittleEndian(randomAccessFile, byteRate)
            writeShortLittleEndian(randomAccessFile, (channels * 2).toShort())
            writeShortLittleEndian(randomAccessFile, 16) // bits per sample

            // data chunk
            randomAccessFile.writeBytes("data")
            writeIntLittleEndian(randomAccessFile, totalDataLen.toInt())
            
            randomAccessFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 写入小端序 Int
     */
    private fun writeIntLittleEndian(file: RandomAccessFile, value: Int) {
        file.writeByte(value and 0xFF)
        file.writeByte((value shr 8) and 0xFF)
        file.writeByte((value shr 16) and 0xFF)
        file.writeByte((value shr 24) and 0xFF)
    }

    /**
     * 写入小端序 Short
     */
    private fun writeShortLittleEndian(file: RandomAccessFile, value: Short) {
        file.writeByte(value.toInt() and 0xFF)
        file.writeByte((value.toInt() shr 8) and 0xFF)
    }
}

