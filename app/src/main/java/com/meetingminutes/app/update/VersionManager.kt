package com.meetingminutes.app.update

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * 版本管理器
 * 负责版本检测、更新下载等功能
 */
class VersionManager(private val context: Context) {
    
    private val client = OkHttpClient()
    private val prefs = context.getSharedPreferences("version_prefs", Context.MODE_PRIVATE)
    
    /**
     * 获取当前版本信息
     */
    fun getCurrentVersion(): VersionInfo {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        
        return VersionInfo(
            versionName = packageInfo.versionName ?: "1.0.0",
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            },
            buildTime = com.meetingminutes.app.BuildConfig.BUILD_TIME,
            gitCommit = com.meetingminutes.app.BuildConfig.GIT_COMMIT
        )
    }
    
    /**
     * 检查更新
     * @param updateUrl 更新检查 URL，返回 JSON 格式的版本信息
     */
    suspend fun checkUpdate(updateUrl: String): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(updateUrl)
                    .build()
                
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext null
                }
                
                val jsonString = response.body?.string() ?: return@withContext null
                val json = JSONObject(jsonString)
                
                val latestVersionCode = json.getInt("versionCode")
                val currentVersionCode = getCurrentVersion().versionCode
                
                if (latestVersionCode > currentVersionCode) {
                    UpdateInfo(
                        versionName = json.getString("versionName"),
                        versionCode = latestVersionCode,
                        downloadUrl = json.getString("downloadUrl"),
                        updateLog = json.optString("updateLog", ""),
                        forceUpdate = json.optBoolean("forceUpdate", false),
                        fileSize = json.optLong("fileSize", 0),
                        md5 = json.optString("md5", "")
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * 下载更新
     */
    suspend fun downloadUpdate(
        updateInfo: UpdateInfo,
        onProgress: (Int) -> Unit
    ): File? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(updateInfo.downloadUrl)
                    .build()
                
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext null
                }
                
                val body = response.body ?: return@withContext null
                val totalBytes = body.contentLength()
                
                val downloadDir = File(context.getExternalFilesDir(null), "downloads")
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs()
                }
                
                val apkFile = File(downloadDir, "update_${updateInfo.versionName}.apk")
                
                body.byteStream().use { input ->
                    FileOutputStream(apkFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0L
                        
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            
                            if (totalBytes > 0) {
                                val progress = (totalBytesRead * 100 / totalBytes).toInt()
                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }
                }
                
                apkFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * 安装 APK
     */
    fun installApk(apkFile: File) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(
                    android.net.Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive"
                )
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        
        context.startActivity(intent)
    }
    
    /**
     * 保存忽略的版本
     */
    fun ignoreVersion(versionCode: Int) {
        prefs.edit().putInt("ignored_version", versionCode).apply()
    }
    
    /**
     * 检查是否已忽略该版本
     */
    fun isVersionIgnored(versionCode: Int): Boolean {
        return prefs.getInt("ignored_version", 0) == versionCode
    }
}

/**
 * 当前版本信息
 */
data class VersionInfo(
    val versionName: String,
    val versionCode: Int,
    val buildTime: String,
    val gitCommit: String
)

/**
 * 更新信息
 */
data class UpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val downloadUrl: String,
    val updateLog: String,
    val forceUpdate: Boolean,
    val fileSize: Long,
    val md5: String
) {
    fun getFormattedSize(): String {
        return when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> String.format("%.2f MB", fileSize / 1024.0 / 1024.0)
        }
    }
}

