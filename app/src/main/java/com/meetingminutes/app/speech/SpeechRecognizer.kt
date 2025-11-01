package com.meetingminutes.app.speech

/**
 * 语音识别接口
 */
interface SpeechRecognizer {
    
    /**
     * 开始实时识别
     * @param dialect 方言类型
     */
    fun startRealTimeRecognition(dialect: String = "mandarin")
    
    /**
     * 发送音频数据
     */
    fun sendAudioData(data: ByteArray, length: Int)
    
    /**
     * 停止识别
     */
    fun stopRecognition()
    
    /**
     * 识别音频文件
     */
    suspend fun recognizeAudioFile(filePath: String, dialect: String = "mandarin"): String
    
    /**
     * 设置识别结果回调
     */
    fun setOnResultListener(listener: (String, Boolean) -> Unit)
    
    /**
     * 设置错误回调
     */
    fun setOnErrorListener(listener: (String) -> Unit)
    
    /**
     * 释放资源
     */
    fun release()
}

/**
 * 支持的方言类型
 */
object DialectType {
    const val MANDARIN = "mandarin"           // 普通话
    const val CANTONESE = "cantonese"         // 粤语
    const val SICHUANESE = "sichuanese"       // 四川话
    const val SHANGHAINESE = "shanghainese"   // 上海话
    const val HOKKIEN = "hokkien"             // 闽南语
    const val HAKKA = "hakka"                 // 客家话
    const val NORTHEASTERN = "northeastern"    // 东北话
    const val TIANJIN = "tianjin"             // 天津话
    const val WUHAN = "wuhan"                 // 武汉话
    const val XIAN = "xian"                   // 西安话
    const val ZHENGZHOU = "zhengzhou"         // 郑州话
    const val NANJING = "nanjing"             // 南京话
    const val NINGXIA = "ningxia"             // 宁夏话 ⭐ 新增
    const val NORTHWEST = "northwest"         // 西北话
    const val GANSU = "gansu"                 // 甘肃话
    const val QINGHAI = "qinghai"             // 青海话
    const val SHAANXI = "shaanxi"             // 陕西话
    
    fun getAllDialects(): List<Pair<String, String>> {
        return listOf(
            MANDARIN to "普通话",
            NINGXIA to "宁夏话 ⭐",  // 宁夏话放在前面，方便选择
            CANTONESE to "粤语",
            SICHUANESE to "四川话",
            SHANGHAINESE to "上海话",
            HOKKIEN to "闽南语",
            HAKKA to "客家话",
            NORTHEASTERN to "东北话",
            TIANJIN to "天津话",
            WUHAN to "武汉话",
            XIAN to "西安话",
            ZHENGZHOU to "郑州话",
            NANJING to "南京话",
            NORTHWEST to "西北话",
            GANSU to "甘肃话",
            QINGHAI to "青海话",
            SHAANXI to "陕西话"
        )
    }
}

