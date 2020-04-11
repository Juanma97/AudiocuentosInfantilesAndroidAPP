package com.alpha.audiocuentosinfantiles

import com.alpha.audiocuentosinfantiles.domain.AudioStory

interface DataConnection {
    fun retrieve(): ArrayList<AudioStory>
}