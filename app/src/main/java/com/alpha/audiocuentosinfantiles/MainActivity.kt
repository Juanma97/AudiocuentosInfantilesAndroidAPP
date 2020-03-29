package com.alpha.audiocuentosinfantiles

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.SearchView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var adapter: AudioCuentoAdapter? = null
        if(Network.networkWork(this)){
            val containerView: GridView = findViewById(R.id.containerView)
            val dataConnection:DataConnection = FirebaseHelper()
            adapter = AudioCuentoAdapter(this, dataConnection.retrieve())

            containerView.adapter = adapter
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView:SearchView = findViewById(R.id.searchView)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Buscar audiocuento..."

        searchView.setOnQueryTextFocusChangeListener{ v, hasFocus ->

        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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
