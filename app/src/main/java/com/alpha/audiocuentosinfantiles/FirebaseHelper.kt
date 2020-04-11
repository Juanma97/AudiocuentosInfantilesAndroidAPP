package com.alpha.audiocuentosinfantiles

import android.util.Log
import com.google.firebase.database.*

class FirebaseHelper {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("audiocuentos")
    val items = ArrayList<AudioStory>()


    fun retrieve(): ArrayList<AudioStory> {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    var acuento: AudioStory = ds.getValue(AudioStory::class.java)!!
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