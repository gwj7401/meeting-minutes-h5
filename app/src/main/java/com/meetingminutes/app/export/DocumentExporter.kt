package com.meetingminutes.app.export

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.kernel.font.PdfFontFactory
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 文档导出器
 */
class DocumentExporter(private val context: Context) {
    
    /**
     * 导出为 TXT 文件
     */
    suspend fun exportToTxt(content: String, fileName: String): File {
        return withContext(Dispatchers.IO) {
            val file = File(getExportDir(), "$fileName.txt")
            file.writeText(content)
            file
        }
    }
    
    /**
     * 导出为 Markdown 文件
     */
    suspend fun exportToMarkdown(
        title: String,
        content: String,
        summary: String,
        fileName: String
    ): File {
        return withContext(Dispatchers.IO) {
            val markdown = buildString {
                appendLine("# $title")
                appendLine()
                
                if (summary.isNotEmpty()) {
                    appendLine("## 会议摘要")
                    appendLine()
                    appendLine(summary)
                    appendLine()
                }
                
                appendLine("## 会议内容")
                appendLine()
                appendLine(content)
            }
            
            val file = File(getExportDir(), "$fileName.md")
            file.writeText(markdown)
            file
        }
    }
    
    /**
     * 导出为 Word 文档
     */
    suspend fun exportToWord(
        title: String,
        content: String,
        summary: String,
        fileName: String
    ): File {
        return withContext(Dispatchers.IO) {
            val file = File(getExportDir(), "$fileName.docx")
            val document = XWPFDocument()
            
            try {
                // 标题
                val titlePara = document.createParagraph()
                val titleRun = titlePara.createRun()
                titleRun.setText(title)
                titleRun.isBold = true
                titleRun.fontSize = 18
                
                // 空行
                document.createParagraph()
                
                // 摘要
                if (summary.isNotEmpty()) {
                    val summaryTitle = document.createParagraph()
                    val summaryTitleRun = summaryTitle.createRun()
                    summaryTitleRun.setText("会议摘要")
                    summaryTitleRun.isBold = true
                    summaryTitleRun.fontSize = 14
                    
                    val summaryPara = document.createParagraph()
                    val summaryRun = summaryPara.createRun()
                    summaryRun.setText(summary)
                    summaryRun.fontSize = 12
                    
                    document.createParagraph()
                }
                
                // 内容标题
                val contentTitle = document.createParagraph()
                val contentTitleRun = contentTitle.createRun()
                contentTitleRun.setText("会议内容")
                contentTitleRun.isBold = true
                contentTitleRun.fontSize = 14
                
                // 内容
                val contentPara = document.createParagraph()
                val contentRun = contentPara.createRun()
                contentRun.setText(content)
                contentRun.fontSize = 12
                
                // 保存文件
                FileOutputStream(file).use { out ->
                    document.write(out)
                }
            } finally {
                document.close()
            }
            
            file
        }
    }
    
    /**
     * 导出为 PDF 文件
     */
    suspend fun exportToPdf(
        title: String,
        content: String,
        summary: String,
        fileName: String
    ): File {
        return withContext(Dispatchers.IO) {
            val file = File(getExportDir(), "$fileName.pdf")
            val writer = PdfWriter(file)
            val pdfDoc = PdfDocument(writer)
            val document = Document(pdfDoc)
            
            try {
                // 使用支持中文的字体
                // 注意：需要在 assets 中放置中文字体文件，如 simhei.ttf
                // val font = PdfFontFactory.createFont("assets/simhei.ttf", PdfEncodings.IDENTITY_H)
                
                // 标题
                val titlePara = Paragraph(title)
                    .setFontSize(18f)
                    .setBold()
                document.add(titlePara)
                
                document.add(Paragraph("\n"))
                
                // 摘要
                if (summary.isNotEmpty()) {
                    val summaryTitle = Paragraph("会议摘要")
                        .setFontSize(14f)
                        .setBold()
                    document.add(summaryTitle)
                    
                    val summaryPara = Paragraph(summary)
                        .setFontSize(12f)
                    document.add(summaryPara)
                    
                    document.add(Paragraph("\n"))
                }
                
                // 内容标题
                val contentTitle = Paragraph("会议内容")
                    .setFontSize(14f)
                    .setBold()
                document.add(contentTitle)
                
                // 内容
                val contentPara = Paragraph(content)
                    .setFontSize(12f)
                document.add(contentPara)
                
            } finally {
                document.close()
            }
            
            file
        }
    }
    
    /**
     * 导出为 HTML 文件
     */
    suspend fun exportToHtml(
        title: String,
        content: String,
        summary: String,
        fileName: String
    ): File {
        return withContext(Dispatchers.IO) {
            val html = buildString {
                appendLine("<!DOCTYPE html>")
                appendLine("<html lang=\"zh-CN\">")
                appendLine("<head>")
                appendLine("    <meta charset=\"UTF-8\">")
                appendLine("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                appendLine("    <title>$title</title>")
                appendLine("    <style>")
                appendLine("        body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }")
                appendLine("        h1 { color: #333; }")
                appendLine("        h2 { color: #666; margin-top: 30px; }")
                appendLine("        p { line-height: 1.6; white-space: pre-wrap; }")
                appendLine("    </style>")
                appendLine("</head>")
                appendLine("<body>")
                appendLine("    <h1>$title</h1>")
                
                if (summary.isNotEmpty()) {
                    appendLine("    <h2>会议摘要</h2>")
                    appendLine("    <p>$summary</p>")
                }
                
                appendLine("    <h2>会议内容</h2>")
                appendLine("    <p>$content</p>")
                appendLine("</body>")
                appendLine("</html>")
            }
            
            val file = File(getExportDir(), "$fileName.html")
            file.writeText(html)
            file
        }
    }
    
    /**
     * 获取导出目录
     */
    private fun getExportDir(): File {
        val dir = File(context.getExternalFilesDir(null), "exports")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}

/**
 * 导出格式枚举
 */
enum class ExportFormat(val displayName: String, val extension: String) {
    TXT("文本文件", "txt"),
    MARKDOWN("Markdown", "md"),
    WORD("Word 文档", "docx"),
    PDF("PDF 文档", "pdf"),
    HTML("HTML 网页", "html")
}

