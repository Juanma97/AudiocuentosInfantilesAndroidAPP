package com.alpha.audiocuentosinfantiles

interface DataConnection {
    fun retrieve(): ArrayList<AudioCuento>
}