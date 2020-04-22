package com.alpha.audiocuentosinfantiles

import android.content.Context
import android.os.AsyncTask
import java.io.BufferedInputStream
import java.net.URL

class DownloadAudioFromUrl(val context: Context): AsyncTask<String, String, String>() {

    override fun doInBackground(vararg p0: String?): String {
        val url  = URL(p0[0])
        val connection = url.openConnection()
        connection.connect()
        val inputStream = BufferedInputStream(url.openStream())
        val filename = "audio.mp3"
        val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val data = ByteArray(1024)
        var count = inputStream.read(data)
        var total = count
        while (count != -1) {
            outputStream.write(data, 0, count)
            count = inputStream.read(data)
            total += count
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
        println("finished saving audio.mp3 to internal storage")
        return "Success"
    }

}