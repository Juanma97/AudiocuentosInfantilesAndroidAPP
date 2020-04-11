package com.alpha.audiocuentosinfantiles

class MusicUtils {

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        return finalTimerString
    }

    fun getProgressSeekBar(currentDuration: Long, totalDuration: Long): Int {
        val progress:Double =
            ((currentDuration.toDouble() / totalDuration) * MAX_PROGRESS) / 100

        return progress.toInt()
    }

    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var totalDuration = totalDuration
        var currentDuration = 0
        totalDuration = (totalDuration / 1000)
        currentDuration =
            (progress.toDouble() / MAX_PROGRESS * totalDuration).toInt()

        return currentDuration * 1000
    }

    companion object {
        const val MAX_PROGRESS = 10000
    }
}