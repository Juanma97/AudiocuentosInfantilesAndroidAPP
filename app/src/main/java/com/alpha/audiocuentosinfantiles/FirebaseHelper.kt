package com.alpha.audiocuentosinfantiles

import android.util.Log
import com.google.firebase.database.*

class FirebaseHelper : DataConnection{

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("audiocuentos")
    val items = ArrayList<AudioCuento>()


    override fun retrieve():ArrayList<AudioCuento> {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fetchData(dataSnapshot)
                Log.d("Firebase: ", "ITEMS")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return items
    }

    override fun fetchData(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.getChildren()) {
            var acuento: AudioCuento = ds.getValue(AudioCuento::class.java)!!
            items.add(acuento)
        }
    }
}