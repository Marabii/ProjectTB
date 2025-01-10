package com.studitradev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo

class PdfViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer l'URL du PDF et le titre à partir de l'intent
        val pdfUrl = intent.getStringExtra("pdfUrl")
        val pdfTitle = intent.getStringExtra("pdfTitle")

        if (pdfUrl != null && pdfTitle != null) {
            // Lancer le visualiseur de PDF
            PdfViewerActivity.launchPdfFromUrl(
                context = this,
                pdfUrl = pdfUrl,
                pdfTitle = pdfTitle,
                enableDownload = false,
                saveTo = saveTo.ASK_EVERYTIME
            )
        } else {
            println("PDF URL or title is missing!")
            finish()
        }
    }
}
