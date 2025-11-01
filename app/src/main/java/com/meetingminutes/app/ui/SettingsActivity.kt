package com.meetingminutes.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.meetingminutes.app.databinding.ActivitySettingsBinding

/**
 * 设置界面
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivitySettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setupToolbar()
            // TODO: 实现设置功能
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "设置界面加载失败: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupToolbar() {
        try {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

