package com.alpha.audiocuentosinfantiles.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alpha.audiocuentosinfantiles.R
import com.alpha.audiocuentosinfantiles.domain.AudioStory
import com.alpha.audiocuentosinfantiles.recyclerview.AudioStoryAdapter
import com.alpha.audiocuentosinfantiles.recyclerview.ClickListener
import com.alpha.audiocuentosinfantiles.recyclerview.RecyclerViewWrapper
import com.alpha.audiocuentosinfantiles.utils.AdmobUtils
import com.alpha.audiocuentosinfantiles.utils.ConnectivityReceiver
import com.alpha.audiocuentosinfantiles.utils.Network
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.onesignal.OneSignal
import java.io.File


class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    var adapter: AudioStoryAdapter? = null
    var containerView: RecyclerView? = null
    var progressBar: ProgressBar? = null
    var noInternetConnection: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        noInternetConnection = findViewById(R.id.textNoInternetConnection)

        var consentInformation: ConsentInformation = ConsentInformation.getInstance(this)
        var publisherIds: Array<String> = arrayOf("pub-0123456789012345")
        consentInformation.requestConsentInfoUpdate(publisherIds, ConsentInfoUp1dateListener {

        })

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()

        setupProgressBar()
        setupContainerView()
        setupAndCheckNetwork()
        setupSearch()

    }

    private fun setupSearch() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = findViewById(R.id.searchView)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = getString(R.string.searchHintSpanish)

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

    private fun setupAndCheckNetwork() {
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        if(!Network.isNetworkActive(this)) showNetworkMessage(false)
    }

    private fun setupContainerView() {
        containerView = RecyclerViewWrapper.setUpRecyclerView(findViewById(R.id.containerView), this)
    }

    private fun setupProgressBar() {
        progressBar = findViewById(R.id.progressBar)
        progressBar?.visibility = View.VISIBLE
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        val audioStories =  ArrayList<AudioStory>()
        val id = 0

        if (!isConnected) {
            Log.d("INTERNET: ", "No connection")
            noInternetConnection?.visibility = View.VISIBLE
            for(file: File in filesDir.listFiles()){
                audioStories.add(AudioStory(id, file.name.replace("_", " ")
                    .replace(".mp3", ""), "", "", ""))
                Log.d("FILE: ", file.name)
            }
            adapter = AudioStoryAdapter(this, audioStories, object : ClickListener {
                    override fun onItemClick(view: View, index: Int) {
                        val intent = Intent(applicationContext, DetailsActivity::class.java)
                        intent.putExtra("AUDIOSTORY", adapter?.items?.get(index))
                        startActivity(intent)
                    }
                })
            progressBar?.visibility = View.INVISIBLE
            containerView?.adapter = adapter
        } else {
            Log.d("INTERNET: ", "Connection")
            getData()
            noInternetConnection?.visibility = View.INVISIBLE
        }
    }

    fun getData() {
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
                adapter = AudioStoryAdapter(context, items, object : ClickListener {
                        override fun onItemClick(view: View, index: Int) {
                            val intent = Intent(applicationContext, DetailsActivity::class.java)
                            intent.putExtra("AUDIOSTORY", adapter?.items?.get(index))
                            startActivity(intent)
                        }
                    })
                progressBar?.visibility = View.INVISIBLE
                containerView?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }
}
