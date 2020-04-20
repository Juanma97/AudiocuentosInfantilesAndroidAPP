package com.alpha.audiocuentosinfantiles.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import com.alpha.audiocuentosinfantiles.R
import com.alpha.audiocuentosinfantiles.domain.AudioStory
import com.alpha.audiocuentosinfantiles.utils.MediaPlayerUtils
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DetailsActivity : AppCompatActivity() {

    private var parentView: View? = null
    private var progressBarLoading: ProgressBar? = null
    private var progressBar: AppCompatSeekBar? = null
    private var textPlaying: TextView? = null
    private var buttonPlay: ImageButton? = null
    private var currentDurationText: TextView? = null
    private var totalDurationText: TextView? = null
    private var audioStoryTitle: TextView? = null
    private var audioStoryImage: CircularImageView? = null
    var mediaPlayer: MediaPlayer = MediaPlayer()
    var handler: Handler = Handler()
    private var totalDuration: Long? = null
    var mediaPlayerUtils: MediaPlayerUtils? = null
    private val storage = FirebaseStorage.getInstance()
    private var storageRef:StorageReference? = null
    private var btnDownload:ImageButton? = null
    private var audioStory: AudioStory? = null

    private fun setMusicPlayerComponents() {
        mediaPlayerUtils = MediaPlayerUtils()

        buttonPlay = findViewById(R.id.btn_play)
        buttonPlay?.isEnabled = false
        parentView = findViewById(R.id.parent_view)
        progressBar = findViewById(R.id.seek_song_progressbar)
        currentDurationText = findViewById(R.id.tv_song_current_duration)
        totalDurationText = findViewById(R.id.total_duration)
        audioStoryTitle = findViewById(R.id.title_details)
        audioStoryImage = findViewById(R.id.image)
        textPlaying = findViewById(R.id.textPlaying)
        btnDownload = findViewById(R.id.btn_download)

        btnDownload?.setOnClickListener {
            storageRef?.downloadUrl?.addOnSuccessListener {
                val url = it.toString()
                writeInternalDemo(url)
            }
        }

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
            progressBarLoading?.visibility = View.INVISIBLE
            buttonPlay?.isEnabled = true
        }
    }

    private fun writeInternalDemo(url: String) { // Path will be: /data/data/<YOUR_APP_PACKAGE_NAME>/hello.txt
        val file = File(filesDir, audioStory?.title + ".mp3")
        // Display the absolute path of Internal Storage
        //this.textViewPathHint.setText(file.getAbsolutePath())
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            //fos.write(this.demoFileContent.getBytes())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed: " + e.message, Toast.LENGTH_LONG).show()
        } finally {
            if (fos != null) {
                try {
                    Toast.makeText(
                        this,
                        "Write successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, "Failed to write!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        audioStory = intent.getSerializableExtra("AUDIOSTORY") as? AudioStory

        progressBarLoading = findViewById(R.id.progressBarLoading)
        progressBarLoading?.visibility = View.VISIBLE

        storageRef = storage.getReferenceFromUrl(audioStory?.url!!)
        setMusicPlayerComponents()
        audioStoryTitle?.text = audioStory?.title

        Glide.with(this).load(audioStory?.url_image).into(audioStoryImage as ImageView)

        progressBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(mUpdateTimeTask)
                val totalDuration:Int = mediaPlayer.duration
                val currentPosition:Int = mediaPlayerUtils?.progressToTimer(seekBar.progress, totalDuration)!!
                mediaPlayer.seekTo(currentPosition)
                handler.post(mUpdateTimeTask)
            }
        })
        buttonPlayerAction()
    }

    private fun buttonPlayerAction() {
        buttonPlay?.setOnClickListener {
            if (mediaPlayer.isPlaying()) {
                textPlaying?.visibility = View.INVISIBLE
                mediaPlayer.pause()
                buttonPlay?.setImageResource(R.drawable.ic_play_arrow)
            } else {
                textPlaying?.visibility = View.VISIBLE
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
        totalDurationText!!.text = mediaPlayerUtils!!.milliSecondsToTimer(totalDuration!!)
        currentDurationText!!.text = mediaPlayerUtils!!.milliSecondsToTimer(currentDuration)
        progressBar!!.progress = mediaPlayerUtils!!.getProgressSeekBar(currentDuration, totalDuration!!)
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

