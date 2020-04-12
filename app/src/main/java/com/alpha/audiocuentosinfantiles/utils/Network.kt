package com.alpha.audiocuentosinfantiles.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

class Network {
    companion object {
        fun isNetworkActive(activity: AppCompatActivity): Boolean {
            val connectivityManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo == null) {
                return false
            } else {
                return networkInfo.isConnected
            }
        }
    }
}