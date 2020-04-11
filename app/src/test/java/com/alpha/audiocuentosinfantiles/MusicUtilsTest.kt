package com.alpha.audiocuentosinfantiles

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MusicUtilsTest {

    var musicUtils: MusicUtils? = null

    @Before
    fun `set up before tests`(){
         musicUtils = MusicUtils()
    }

    @Test
    fun `given a current and total duration should return the progress`(){
        val progress = musicUtils?.getProgressSeekBar(30L, 100L)
        assertEquals(30, progress)
    }

    @Test
    fun `given a milliseconds should return the time`(){
        val time = musicUtils?.milliSecondsToTimer(10000)
        assertEquals("0:10", time)
    }
}