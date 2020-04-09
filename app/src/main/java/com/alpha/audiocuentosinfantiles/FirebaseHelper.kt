package com.alpha.audiocuentosinfantiles

import android.util.Log
import com.google.firebase.database.*

class FirebaseHelper {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("audiocuentos")
    val items = ArrayList<AudioCuento>()


    fun retrieve(): ArrayList<AudioCuento> {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    var acuento: AudioCuento = ds.getValue(AudioCuento::class.java)!!
                    items.add(acuento)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled" + error.message)
            }
        })

        return items
    }

}