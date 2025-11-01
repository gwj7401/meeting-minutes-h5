package com.meetingminutes.app.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.meetingminutes.app.MeetingApp
import com.meetingminutes.app.R
import com.meetingminutes.app.data.model.MeetingRecord
import com.meetingminutes.app.databinding.ActivityMainBinding
import com.meetingminutes.app.update.VersionManager
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MeetingRecordAdapter
    private val database by lazy { (application as MeetingApp).database }
    private val versionManager by lazy { VersionManager(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setSupportActionBar(binding.toolbar)

            setupRecyclerView()
            setupFab()
            requestPermissions()
            loadRecords()
            // checkUpdate()  // 暂时注释掉，避免网络问题
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = MeetingRecordAdapter(
            onItemClick = { record ->
                // 打开编辑界面
                val intent = Intent(this, EditorActivity::class.java)
                intent.putExtra("record_id", record.id)
                startActivity(intent)
            },
            onDeleteClick = { record ->
                showDeleteConfirmDialog(record)
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupFab() {
        binding.fabRecord.setOnClickListener {
            // 开始录音
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }
        
        binding.fabImport.setOnClickListener {
            // 导入音频文件
            importAudioFile()
        }
    }
    
    private fun requestPermissions() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .request { allGranted, _, _ ->
                if (!allGranted) {
                    Toast.makeText(this, "需要授予权限才能使用完整功能", Toast.LENGTH_SHORT).show()
                }
            }
    }
    
    private fun loadRecords() {
        lifecycleScope.launch {
            database.meetingRecordDao().getAllRecords().collectLatest { records ->
                adapter.submitList(records)
                
                // 显示/隐藏空状态
                if (records.isEmpty()) {
                    binding.emptyView.visibility = android.view.View.VISIBLE
                    binding.recyclerView.visibility = android.view.View.GONE
                } else {
                    binding.emptyView.visibility = android.view.View.GONE
                    binding.recyclerView.visibility = android.view.View.VISIBLE
                }
            }
        }
    }
    
    private fun importAudioFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, REQUEST_IMPORT_AUDIO)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMPORT_AUDIO && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // 处理导入的音频文件
                Toast.makeText(this, "正在导入音频文件...", Toast.LENGTH_SHORT).show()
                // TODO: 实现音频导入和识别
            }
        }
    }
    
    private fun showDeleteConfirmDialog(record: MeetingRecord) {
        MaterialAlertDialogBuilder(this)
            .setTitle("删除确认")
            .setMessage("确定要删除「${record.title}」吗？")
            .setPositiveButton("删除") { _, _ ->
                deleteRecord(record)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun deleteRecord(record: MeetingRecord) {
        lifecycleScope.launch {
            database.meetingRecordDao().delete(record)
            Toast.makeText(this@MainActivity, "已删除", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun checkUpdate() {
        lifecycleScope.launch {
            try {
                // TODO: 配置实际的更新检查 URL
                val updateUrl = "https://your-server.com/api/version/check"
                val updateInfo = versionManager.checkUpdate(updateUrl)
                
                if (updateInfo != null && !versionManager.isVersionIgnored(updateInfo.versionCode)) {
                    showUpdateDialog(updateInfo)
                }
            } catch (e: Exception) {
                // 静默失败
            }
        }
    }
    
    private fun showUpdateDialog(updateInfo: com.meetingminutes.app.update.UpdateInfo) {
        MaterialAlertDialogBuilder(this)
            .setTitle("发现新版本 ${updateInfo.versionName}")
            .setMessage(updateInfo.updateLog)
            .setPositiveButton("立即更新") { _, _ ->
                // TODO: 实现更新下载
            }
            .setNegativeButton("稍后提醒", null)
            .setNeutralButton("忽略此版本") { _, _ ->
                versionManager.ignoreVersion(updateInfo.versionCode)
            }
            .setCancelable(!updateInfo.forceUpdate)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return try {
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.action_about -> {
                    showAboutDialog()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "操作失败: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
    
    private fun showAboutDialog() {
        val versionInfo = versionManager.getCurrentVersion()
        MaterialAlertDialogBuilder(this)
            .setTitle("关于")
            .setMessage("""
                会议纪要 App
                
                版本：${versionInfo.versionName} (${versionInfo.versionCode})
                构建时间：${versionInfo.buildTime}
                Git Commit：${versionInfo.gitCommit}
                
                功能特性：
                • 实时录音转文字
                • 支持多种方言识别
                • AI 智能总结
                • 多格式文档导出
                • 自动版本管理
            """.trimIndent())
            .setPositiveButton("确定", null)
            .show()
    }
    
    companion object {
        private const val REQUEST_IMPORT_AUDIO = 1001
    }
}

