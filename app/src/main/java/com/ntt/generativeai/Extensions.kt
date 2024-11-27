package com.ntt.generativeai

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File

fun File.getBitmap() = BitmapFactory.decodeFile(absolutePath)

fun String.createPdfFromText(context: Context, fileName: String = "document.pdf") {
    val pdfDocument = PdfDocument()

    // Set page dimensions (A4 size in pixels at 72 DPI)
    val pageWidth = 595
    val pageHeight = 842

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 12f
    }

    // Split text into lines that fit page width
    val textLines = calculateTextLines(this, paint, pageWidth - 40) // 20px margins

    // Calculate how many lines fit per page
    val lineHeight = paint.fontSpacing
    val linesPerPage = ((pageHeight - 40) / lineHeight).toInt() // 20px margins

    // Create pages as needed
    var currentLine = 0
    while (currentLine < textLines.size) {
        val pageInfo =
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentLine / linesPerPage + 1)
                .create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Draw lines for current page
        var y = 20f
        for (i in 0 until linesPerPage) {
            if (currentLine >= textLines.size) break
            canvas.drawText(textLines[currentLine], 20f, y + lineHeight, paint)
            y += lineHeight
            currentLine++
        }

        pdfDocument.finishPage(page)
    }

    // Save PDF to file
    val file = File(context.filesDir, fileName)
    pdfDocument.writeTo(file.outputStream())
    pdfDocument.close()

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share PDF"))
}

private fun calculateTextLines(text: String, paint: Paint, maxWidth: Int): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder()

    for (word in words) {
        if (paint.measureText(currentLine.toString() + word) < maxWidth) {
            if (currentLine.isNotEmpty()) currentLine.append(" ")
            currentLine.append(word)
        } else {
            lines.add(currentLine.toString())
            currentLine = StringBuilder(word)
        }
    }

    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.toString())
    }

    return lines
}