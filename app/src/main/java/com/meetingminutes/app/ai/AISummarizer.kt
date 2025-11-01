package com.meetingminutes.app.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * AI 会议纪要总结器
 * 支持多种大模型 API：OpenAI、通义千问、文心一言、讯飞星火等
 */
class AISummarizer {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    /**
     * 使用 OpenAI API 生成摘要
     */
    suspend fun summarizeWithOpenAI(
        content: String,
        apiKey: String,
        apiUrl: String = "https://api.openai.com/v1/chat/completions"
    ): String {
        return withContext(Dispatchers.IO) {
            val prompt = buildSummaryPrompt(content)
            
            val jsonBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "你是一个专业的会议纪要助手，擅长总结会议内容。")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.7)
                put("max_tokens", 1000)
            }
            
            val request = Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("API 请求失败: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: throw IOException("响应为空")
            val jsonResponse = JSONObject(responseBody)
            
            jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
    
    /**
     * 使用通义千问 API 生成摘要
     */
    suspend fun summarizeWithQianwen(
        content: String,
        apiKey: String
    ): String {
        return withContext(Dispatchers.IO) {
            val prompt = buildSummaryPrompt(content)
            
            val jsonBody = JSONObject().apply {
                put("model", "qwen-turbo")
                put("input", JSONObject().apply {
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", "你是一个专业的会议纪要助手。")
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        })
                    })
                })
                put("parameters", JSONObject().apply {
                    put("result_format", "message")
                })
            }
            
            val request = Request.Builder()
                .url("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("API 请求失败: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: throw IOException("响应为空")
            val jsonResponse = JSONObject(responseBody)
            
            jsonResponse
                .getJSONObject("output")
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
    
    /**
     * 使用文心一言 API 生成摘要
     */
    suspend fun summarizeWithErnie(
        content: String,
        accessToken: String
    ): String {
        return withContext(Dispatchers.IO) {
            val prompt = buildSummaryPrompt(content)
            
            val jsonBody = JSONObject().apply {
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }
            
            val request = Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=$accessToken")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("API 请求失败: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: throw IOException("响应为空")
            val jsonResponse = JSONObject(responseBody)
            
            jsonResponse.getString("result").trim()
        }
    }
    
    /**
     * 构建总结提示词
     */
    private fun buildSummaryPrompt(content: String): String {
        return """
请对以下会议内容进行总结，要求：

1. 提取会议的主要议题和讨论要点
2. 总结关键决策和行动项
3. 列出重要的时间节点和责任人（如果有）
4. 使用清晰的结构化格式
5. 保持简洁，突出重点

会议内容：
$content

请按以下格式输出：

【会议主题】
（简要概括会议主题）

【主要议题】
1. ...
2. ...

【关键决策】
1. ...
2. ...

【行动项】
1. ...
2. ...

【其他要点】
...
        """.trimIndent()
    }
    
    /**
     * 本地简单总结（不依赖 API）
     */
    fun simpleLocalSummary(content: String): String {
        if (content.isEmpty()) {
            return "暂无内容"
        }
        
        // 简单的本地总结逻辑
        val lines = content.split("\n").filter { it.isNotBlank() }
        val wordCount = content.length
        val sentenceCount = content.split("[。！？]".toRegex()).size
        
        return buildString {
            appendLine("【会议概况】")
            appendLine("总字数：$wordCount 字")
            appendLine("总句数：约 $sentenceCount 句")
            appendLine()
            appendLine("【内容预览】")
            appendLine(content.take(200) + if (content.length > 200) "..." else "")
            appendLine()
            appendLine("提示：使用 AI 总结功能可获得更详细的会议摘要")
        }
    }
}

/**
 * AI 模型类型
 */
enum class AIModel(val displayName: String) {
    OPENAI("OpenAI GPT"),
    QIANWEN("通义千问"),
    ERNIE("文心一言"),
    LOCAL("本地总结")
}

