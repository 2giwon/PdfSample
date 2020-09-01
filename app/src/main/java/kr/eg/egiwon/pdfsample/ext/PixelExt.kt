package kr.eg.egiwon.pdfsample.ext

import android.content.Context
import android.util.TypedValue

fun Float.pixelToDP(context: Context): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
