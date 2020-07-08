package kr.eg.egiwon.pdfsample.pdfview

import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.base.BaseActivity
import kr.eg.egiwon.pdfsample.databinding.ActivityPdfBinding

@AndroidEntryPoint
class PdfViewActivity : BaseActivity<ActivityPdfBinding, PdfViewModel>(
    R.layout.activity_pdf
) {
    override val viewModel: PdfViewModel by viewModels()

}