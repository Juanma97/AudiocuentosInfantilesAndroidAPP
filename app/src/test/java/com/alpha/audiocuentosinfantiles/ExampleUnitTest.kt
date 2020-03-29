package com.alpha.audiocuentosinfantiles

import org.junit.Test

import org.junit.Assert.*

class FirebaseHelperTest {

    @Test
    fun retrieveDataSuccesffully() {
        assertNotNull(FirebaseHelper().retrieve())
    }
}
