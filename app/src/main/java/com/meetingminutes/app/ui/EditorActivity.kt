package com.meetingminutes.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.meetingminutes.app.MeetingApp
import com.meetingminutes.app.ai.AIModel
import com.meetingminutes.app.ai.AISummarizer
import com.meetingminutes.app.data.model.MeetingRecord
import com.meetingminutes.app.databinding.ActivityEditorBinding
import com.meetingminutes.app.export.DocumentExporter
import com.meetingminutes.app.export.ExportFormat
import kotlinx.coroutines.launch

/**
 * 文字编辑界面
 */
class EditorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEditorBinding
    private val database by lazy { (application as MeetingApp).database }
    private val aiSummarizer = AISummarizer()
    private val documentExporter by lazy { DocumentExporter(this) }
    
    private var currentRecord: MeetingRecord? = null
    private var recordId: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        recordId = intent.getLongExtra("record_id", 0)
        
        setupToolbar()
        setupButtons()
        loadRecord()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupButtons() {
        // 生成 AI 摘要
        binding.btnGenerateSummary.setOnClickListener {
            generateSummary()
        }
        
        // 导出文档
        binding.btnExport.setOnClickListener {
            showExportDialog()
        }
        
        // 保存
        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }
    
    private fun loadRecord() {
        lifecycleScope.launch {
            val record = database.meetingRecordDao().getRecordById(recordId)
            if (record != null) {
                currentRecord = record
                displayRecord(record)
            } else {
                Toast.makeText(this@EditorActivity, "记录不存在", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun displayRecord(record: MeetingRecord) {
        binding.apply {
            etTitle.setText(record.title)
            etContent.setText(record.content)
            etSummary.setText(record.summary)
            
            tvDate.text = record.getFormattedDate()
            tvDuration.text = "时长: ${record.getFormattedDuration()}"
        }
    }
    
    private fun generateSummary() {
        val content = binding.etContent.text.toString()
        if (content.isEmpty()) {
            Toast.makeText(this, "内容为空，无法生成摘要", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 显示选择 AI 模型对话框
        val models = AIModel.values()
        val modelNames = models.map { it.displayName }.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("选择 AI 模型")
            .setItems(modelNames) { _, which ->
                val selectedModel = models[which]
                performSummary(content, selectedModel)
            }
            .show()
    }
    
    private fun performSummary(content: String, model: AIModel) {
        binding.btnGenerateSummary.isEnabled = false
        binding.btnGenerateSummary.text = "生成中..."
        
        lifecycleScope.launch {
            try {
                val summary = when (model) {
                    AIModel.OPENAI -> {
                        // TODO: 从设置中获取 API Key
                        val apiKey = "your_openai_api_key"
                        aiSummarizer.summarizeWithOpenAI(content, apiKey)
                    }
                    AIModel.QIANWEN -> {
                        val apiKey = "your_qianwen_api_key"
                        aiSummarizer.summarizeWithQianwen(content, apiKey)
                    }
                    AIModel.ERNIE -> {
                        val accessToken = "your_ernie_access_token"
                        aiSummarizer.summarizeWithErnie(content, accessToken)
                    }
                    AIModel.LOCAL -> {
                        aiSummarizer.simpleLocalSummary(content)
                    }
                }
                
                binding.etSummary.setText(summary)
                Toast.makeText(this@EditorActivity, "摘要生成成功", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@EditorActivity,
                    "生成失败: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnGenerateSummary.isEnabled = true
                binding.btnGenerateSummary.text = "生成摘要"
            }
        }
    }
    
    private fun showExportDialog() {
        val formats = ExportFormat.values()
        val formatNames = formats.map { "${it.displayName} (${it.extension})" }.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("选择导出格式")
            .setItems(formatNames) { _, which ->
                val selectedFormat = formats[which]
                exportDocument(selectedFormat)
            }
            .show()
    }
    
    private fun exportDocument(format: ExportFormat) {
        val record = currentRecord ?: return
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()
        val summary = binding.etSummary.text.toString()
        
        if (content.isEmpty()) {
            Toast.makeText(this, "内容为空，无法导出", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val fileName = "${title}_${System.currentTimeMillis()}"
                
                val file = when (format) {
                    ExportFormat.TXT -> documentExporter.exportToTxt(content, fileName)
                    ExportFormat.MARKDOWN -> documentExporter.exportToMarkdown(
                        title, content, summary, fileName
                    )
                    ExportFormat.WORD -> documentExporter.exportToWord(
                        title, content, summary, fileName
                    )
                    ExportFormat.PDF -> documentExporter.exportToPdf(
                        title, content, summary, fileName
                    )
                    ExportFormat.HTML -> documentExporter.exportToHtml(
                        title, content, summary, fileName
                    )
                }
                
                Toast.makeText(
                    this@EditorActivity,
                    "导出成功: ${file.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
                
                // 分享文件
                shareFile(file)
                
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@EditorActivity,
                    "导出失败: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun shareFile(file: java.io.File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(intent, "分享文件"))
    }
    
    private fun saveChanges() {
        val record = currentRecord ?: return
        
        val updatedRecord = record.copy(
            title = binding.etTitle.text.toString(),
            content = binding.etContent.text.toString(),
            summary = binding.etSummary.text.toString(),
            hasSummary = binding.etSummary.text.toString().isNotEmpty(),
            updateTime = System.currentTimeMillis()
        )
        
        lifecycleScope.launch {
            database.meetingRecordDao().update(updatedRecord)
            Toast.makeText(this@EditorActivity, "已保存", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    override fun onBackPressed() {
        // 检查是否有未保存的更改
        val hasChanges = currentRecord?.let { record ->
            record.title != binding.etTitle.text.toString() ||
            record.content != binding.etContent.text.toString() ||
            record.summary != binding.etSummary.text.toString()
        } ?: false
        
        if (hasChanges) {
            MaterialAlertDialogBuilder(this)
                .setTitle("保存更改")
                .setMessage("是否保存更改？")
                .setPositiveButton("保存") { _, _ ->
                    saveChanges()
                }
                .setNegativeButton("不保存") { _, _ ->
                    super.onBackPressed()
                }
                .setNeutralButton("取消", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
}

