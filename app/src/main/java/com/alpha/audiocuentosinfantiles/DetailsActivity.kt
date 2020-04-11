package com.alpha.audiocuentosinfantiles

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import java.io.File


class DetailsActivity : AppCompatActivity() {

    var parent_view: View? = null
    var seek_song_progressbar: AppCompatSeekBar? = null

    var btn_play: ImageButton? = null

    var tv_song_current_duration: TextView? = null
    var tv_song_total_duration: TextView? = null
    var title_details: TextView? = null
    var image: CircularImageView? = null

    var mediaPlayer: MediaPlayer = MediaPlayer()

    var handler: Handler = Handler()

    var totalDuration: Long? = null
    var utils: MusicUtils? = null
    val storage = FirebaseStorage.getInstance()
    var storageRef:StorageReference? = null

    fun setMusicPlayerComponents() {
        utils = MusicUtils()
        btn_play = findViewById(R.id.btn_play)
        parent_view = findViewById(R.id.parent_view)
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar)
        tv_song_current_duration = findViewById(R.id.tv_song_current_duration)
        tv_song_total_duration = findViewById(R.id.total_duration)
        title_details = findViewById(R.id.title_details)
        image = findViewById(R.id.image)

        mediaPlayer.setOnCompletionListener {
            btn_play?.setImageResource(R.drawable.ic_play_arrow)
        }

        mediaPlayer.setOnPreparedListener {
            totalDuration = mediaPlayer.duration.toLong()
            updateTimerAndSeekbar()
        }

        storageRef?.downloadUrl?.addOnSuccessListener {
            val url = it.toString()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val audioCuento = intent.getSerializableExtra("AUDIOCUENTO") as? AudioCuento
        storageRef = storage.getReferenceFromUrl(audioCuento?.url!!)
        setMusicPlayerComponents()
        title_details?.text = audioCuento.title

        Glide.with(this).load(audioCuento.url_image).into(image as ImageView)

        seek_song_progressbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
                val totalDuration:Int = mediaPlayer.duration
                val currentPosition:Int = utils?.progressToTimer(seekBar.progress, totalDuration)!!
                mediaPlayer.seekTo(currentPosition)
                handler.post(mUpdateTimeTask)
            }
        })
        buttonPlayerAction()
    }

    private fun buttonPlayerAction() {
        btn_play?.setOnClickListener {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause()
                btn_play?.setImageResource(R.drawable.ic_play_arrow)
            } else {
                mediaPlayer.start()
                btn_play?.setImageResource(R.drawable.ic_pause)
                handler.post(mUpdateTimeTask)
            }
            rotateTheDisk()
        }
    }

    private fun rotateTheDisk() {
        if (!mediaPlayer.isPlaying()) return
        image!!.animate().setDuration(100).rotation(image!!.rotation + 2f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    rotateTheDisk()
                    super.onAnimationEnd(animation)
                }
            })
    }

    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            updateTimerAndSeekbar()
            if (mediaPlayer.isPlaying()) {
                handler.postDelayed(this, 100)
            }
        }
    }

    private fun updateTimerAndSeekbar() {
        val currentDuration: Long = mediaPlayer.currentPosition.toLong()
        Log.d("MUSIC", totalDuration.toString())
        Log.d("MUSIC", currentDuration.toString())
        tv_song_total_duration!!.text = utils!!.milliSecondsToTimer(totalDuration!!)
        tv_song_current_duration!!.text = utils!!.milliSecondsToTimer(currentDuration)
        seek_song_progressbar!!.progress = utils!!.getProgressSeekBar(currentDuration, totalDuration!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(mUpdateTimeTask)
        mediaPlayer.release()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

