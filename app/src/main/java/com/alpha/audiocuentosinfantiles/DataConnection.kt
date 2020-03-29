package com.alpha.audiocuentosinfantiles

import com.google.firebase.database.DataSnapshot

interface DataConnection {

    fun retrieve():ArrayList<AudioCuento>

    fun fetchData(dataSnapshot: DataSnapshot)
}