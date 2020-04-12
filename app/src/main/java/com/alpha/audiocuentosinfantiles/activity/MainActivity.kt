package com.alpha.audiocuentosinfantiles.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alpha.audiocuentosinfantiles.R
import com.alpha.audiocuentosinfantiles.domain.AudioStory
import com.alpha.audiocuentosinfantiles.recyclerview.AudioStoryAdapter
import com.alpha.audiocuentosinfantiles.recyclerview.ClickListener
import com.alpha.audiocuentosinfantiles.recyclerview.RecyclerViewWrapper
import com.alpha.audiocuentosinfantiles.utils.Network
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    var adapter: AudioStoryAdapter? = null
    var containerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!Network.isNetworkActive(this)){
            startActivity(Intent(this, NoNetworkActivity::class.java))
        }

        containerView = RecyclerViewWrapper
            .setUpRecyclerView(findViewById(R.id.containerView), this)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("audiocuentos")
        val items = ArrayList<AudioStory>()
        val context = this
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    val audioStory: AudioStory = ds.getValue(
                        AudioStory::class.java)!!
                    items.add(audioStory)
                }
                adapter =
                    AudioStoryAdapter(
                        context,
                        items,
                        object :
                            ClickListener {
                            override fun onItemClick(view: View, index: Int) {
                                val intent = Intent(applicationContext, DetailsActivity::class.java)
                                intent.putExtra("AUDIOSTORY", adapter?.items?.get(index))
                                startActivity(intent)
                            }

                        })
                containerView?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled" + error.message)
            }
        })


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = findViewById(R.id.searchView)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Buscar audiocuento..."

        searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->

        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                adapter?.filter(p0!!)
                adapter?.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
        })
    }
}
