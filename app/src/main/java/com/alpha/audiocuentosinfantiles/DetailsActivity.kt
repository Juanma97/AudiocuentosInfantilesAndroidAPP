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
    //private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private var pause: Boolean = false

    var utils: MusicUtils? = null
    val storage = FirebaseStorage.getInstance()
    var storageRef:StorageReference? = null

    fun setMusicPlayerComponents() {
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

        storageRef?.downloadUrl?.addOnSuccessListener {
            val url = it.toString()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }

        utils = MusicUtils()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val audioCuento = intent.getSerializableExtra("AUDIOCUENTO") as? AudioCuento
        storageRef = storage.getReferenceFromUrl(audioCuento?.url!!)
        setMusicPlayerComponents()
        title_details?.text = audioCuento.title

        Glide.with(this).load(audioCuento.url_image).into(image as ImageView)
        // Seek bar change listener
        seek_song_progressbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
                var totalDuration:Int = mediaPlayer.duration
                var currentPosition:Int = utils?.progressToTimer(seekBar.progress, totalDuration)!!
                mediaPlayer.seekTo(currentPosition)
                handler.post(mUpdateTimeTask)
            }
        })
        buttonPlayerAction()
        updateTimerAndSeekbar()
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


    private fun toggleButtonColor(bt: ImageButton): Boolean {
        val selected = bt.getTag(bt.id) as String
        return if (selected != null) { // selected
            bt.setColorFilter(
                resources.getColor(R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP
            )
            bt.setTag(bt.id, null)
            false
        } else {
            bt.setTag(bt.id, "selected")
            bt.setColorFilter(
                resources.getColor(R.color.colorPrimaryDark),
                PorterDuff.Mode.SRC_ATOP
            )
            true
        }
    }

    private fun updateTimerAndSeekbar() {
        val totalDuration: Long = mediaPlayer.getDuration().toLong()
        val currentDuration: Long = mediaPlayer.getCurrentPosition().toLong()
        tv_song_total_duration!!.text = utils!!.milliSecondsToTimer(totalDuration)
        tv_song_current_duration!!.text = utils!!.milliSecondsToTimer(currentDuration)
        seek_song_progressbar!!.progress =
            utils!!.getProgressSeekBar(currentDuration, totalDuration)
    }

    private fun downloadFile(storage: FirebaseStorage, url: String) {
        val httpsReference = storage.getReferenceFromUrl(url)

        val localFile = File.createTempFile("music", "mp3")


        httpsReference.getFile(localFile).addOnSuccessListener {
            Log.d("STORAGE", "File created")
            Log.d("STORAGE", "Bytes : " + it.bytesTransferred)
        }.addOnFailureListener {
            // Handle any errors
            Log.d("STORAGE", "File NOT created")
        }
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

