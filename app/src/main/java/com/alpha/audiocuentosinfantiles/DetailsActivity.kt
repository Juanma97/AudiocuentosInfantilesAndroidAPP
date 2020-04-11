package com.alpha.audiocuentosinfantiles

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView


class DetailsActivity : AppCompatActivity() {

    var parentView: View? = null
    var progressBar: AppCompatSeekBar? = null

    var buttonPlay: ImageButton? = null

    var currentDurationText: TextView? = null
    var totalDurationText: TextView? = null
    var audioStoryTitle: TextView? = null
    var audioStoryImage: CircularImageView? = null

    var mediaPlayer: MediaPlayer = MediaPlayer()

    var handler: Handler = Handler()

    var totalDuration: Long? = null
    var musicUtils: MusicUtils? = null
    val storage = FirebaseStorage.getInstance()
    var storageRef:StorageReference? = null

    fun setMusicPlayerComponents() {
        musicUtils = MusicUtils()
        buttonPlay = findViewById(R.id.btn_play)
        parentView = findViewById(R.id.parent_view)
        progressBar = findViewById(R.id.seek_song_progressbar)
        currentDurationText = findViewById(R.id.tv_song_current_duration)
        totalDurationText = findViewById(R.id.total_duration)
        audioStoryTitle = findViewById(R.id.title_details)
        audioStoryImage = findViewById(R.id.image)

        mediaPlayer.setOnCompletionListener {
            buttonPlay?.setImageResource(R.drawable.ic_play_arrow)
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

        val audioStory = intent.getSerializableExtra("AUDIOSTORY") as? AudioStory
        storageRef = storage.getReferenceFromUrl(audioStory?.url!!)
        setMusicPlayerComponents()
        audioStoryTitle?.text = audioStory.title

        Glide.with(this).load(audioStory.url_image).into(audioStoryImage as ImageView)

        progressBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
                val totalDuration:Int = mediaPlayer.duration
                val currentPosition:Int = musicUtils?.progressToTimer(seekBar.progress, totalDuration)!!
                mediaPlayer.seekTo(currentPosition)
                handler.post(mUpdateTimeTask)
            }
        })
        buttonPlayerAction()
    }

    private fun buttonPlayerAction() {
        buttonPlay?.setOnClickListener {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause()
                buttonPlay?.setImageResource(R.drawable.ic_play_arrow)
            } else {
                mediaPlayer.start()
                buttonPlay?.setImageResource(R.drawable.ic_pause)
                handler.post(mUpdateTimeTask)
            }
            rotateTheDisk()
        }
    }

    private fun rotateTheDisk() {
        if (!mediaPlayer.isPlaying()) return
        audioStoryImage!!.animate().setDuration(100).rotation(audioStoryImage!!.rotation + 2f)
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
        totalDurationText!!.text = musicUtils!!.milliSecondsToTimer(totalDuration!!)
        currentDurationText!!.text = musicUtils!!.milliSecondsToTimer(currentDuration)
        progressBar!!.progress = musicUtils!!.getProgressSeekBar(currentDuration, totalDuration!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(mUpdateTimeTask)
        mediaPlayer.release()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}

