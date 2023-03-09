package com.example.digiboxxdemo

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import okio.Source
import java.io.File

class UploadRequestBody(val file: File, private val contentType: String): RequestBody() {

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 256 * 1024

    }

    private var source: Source? = null
    private var sink: BufferedSink? = null

    override fun contentType() = "$contentType/*".toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val source = file.source()


        this.source = source
        this.sink = sink

        var total: Long = 0
        var read: Long
        val handler = Handler(Looper.getMainLooper())
        while (source.read(sink.buffer, DEFAULT_BUFFER_SIZE.toLong()).also { read = it }.toInt() != -1) {
            total += read
//            handler.post(ProgressUpdate(total, length))
            sink.flush()
        }
    }
}