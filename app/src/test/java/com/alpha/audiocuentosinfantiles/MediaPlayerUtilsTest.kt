package com.alpha.audiocuentosinfantiles

import com.alpha.audiocuentosinfantiles.utils.MediaPlayerUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MediaPlayerUtilsTest {

    var mediaPlayerUtils: MediaPlayerUtils? = null

    @Before
    fun `set up before tests`(){
         mediaPlayerUtils =
             MediaPlayerUtils()
    }

    @Test
    fun `given a current and total duration should return the progress`(){
        val progress = mediaPlayerUtils?.getProgressSeekBar(30L, 100L)
        assertEquals(30, progress)
    }

    @Test
    fun `given a milliseconds should return the time`(){
        val time = mediaPlayerUtils?.milliSecondsToTimer(10000)
        assertEquals("0:10", time)
    }
}