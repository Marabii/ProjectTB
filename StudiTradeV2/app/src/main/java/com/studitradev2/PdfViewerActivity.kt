package com.studitradev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rajat.pdfviewer.PdfViewerActivity

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
                saveTo = PdfViewerActivity.SaveTo.ASK_EVERYTIME,
                enableDownload = true
            )
        } else {
            // Terminer l'activité si les paramètres sont manquants
            finish()
        }
    }
}
