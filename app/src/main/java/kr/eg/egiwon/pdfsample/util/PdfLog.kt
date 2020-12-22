package kr.eg.egiwon.pdfsample.util

import android.util.Log
import kr.eg.egiwon.pdfsample.BuildConfig

object PdfLog {
    private const val ENABLED = BuildConfig.logEnabled

    fun v(tag: String, message: String) {
        if (ENABLED) Log.v(tag, message)
    }

    fun e(tag: String, message: String) {
        if (ENABLED) Log.e(tag, message)
    }

    fun i(tag: String, message: String) {
        if (ENABLED) Log.i(tag, message)
    }

    fun d(tag: String, message: String) {
        if (ENABLED) Log.e(tag, message)
    }

    fun w(tag: String, message: String) {
        if (ENABLED) Log.w(tag, message)
    }

    fun wtf(tag: String, message: String) {
        if (ENABLED) Log.wtf(tag, message)
    }
}