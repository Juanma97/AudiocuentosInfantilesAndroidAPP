package com.alpha.audiocuentosinfantiles

import android.content.Context
import android.os.AsyncTask
import java.io.BufferedInputStream
import java.net.URL

class DownloadAudioFromUrl(val context: Context, val fileName: String): AsyncTask<String, String, String>() {

    override fun doInBackground(vararg url: String?): String {
        val url  = URL(url[0])
        val connection = url.openConnection()
        connection.connect()
        val inputStream = BufferedInputStream(url.openStream())
        val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
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
        return "Success"
    }

}