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
import com.alpha.audiocuentosinfantiles.DownloadAudioFromUrl
import com.alpha.audiocuentosinfantiles.R
import com.alpha.audiocuentosinfantiles.domain.AudioStory
import com.alpha.audiocuentosinfantiles.utils.MediaPlayerUtils
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import java.io.*


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
    private var relativeLayout: RelativeLayout? = null

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
        btnDownload?.visibility = View.INVISIBLE

    }

    private fun prepareButtonDownload() {
        btnDownload?.visibility = View.VISIBLE
        btnDownload?.setOnClickListener {
            storageRef?.downloadUrl?.addOnSuccessListener {
                val url = it.toString()
                DownloadAudioFromUrl(this, audioStory?.title?.replace(" ", "_") + ".mp3")
                    .execute(url)

                Snackbar.make(relativeLayout!!, "Descargando audiocuento ...", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun prepareMediaPlayerStreaming() {
        storageRef?.downloadUrl?.addOnSuccessListener {
            val url = it.toString()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            progressBarLoading?.visibility = View.INVISIBLE
            buttonPlay?.isEnabled = true
        }

        mediaPlayer.setOnCompletionListener {
            buttonPlay?.setImageResource(R.drawable.ic_play_arrow)
        }

        mediaPlayer.setOnPreparedListener {
            totalDuration = mediaPlayer.duration.toLong()
            updateTimerAndSeekbar()
        }
    }


    private fun readFileInternalStorage(fileName: String) {
        val file = File(filesDir, fileName.replace(" ", "_"))
        if (!file.exists()) return

        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(file)
            mediaPlayer.setDataSource(fileInputStream.fd)
            mediaPlayer.prepare()
            progressBarLoading?.visibility = View.INVISIBLE
            buttonPlay?.isEnabled = true
        } catch (e: java.lang.Exception) {

        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        progressBarLoading = findViewById(R.id.progressBarLoading)
        progressBarLoading?.visibility = View.VISIBLE
        relativeLayout = findViewById(R.id.parent_view)

        audioStory = intent.getSerializableExtra("AUDIOSTORY") as? AudioStory

        setMusicPlayerComponents()

        if(audioStory?.url?.isEmpty()!!){
            Glide.with(this).load(R.drawable.music_disk).into(audioStoryImage as ImageView)
            readFileInternalStorage(audioStory?.title!!)
            mediaPlayer.setOnCompletionListener {
                buttonPlay?.setImageResource(R.drawable.ic_play_arrow)
            }

            mediaPlayer.setOnPreparedListener {
                totalDuration = mediaPlayer.duration.toLong()
                updateTimerAndSeekbar()
            }
            progressBarLoading?.visibility = View.INVISIBLE
        }else{
            storageRef = storage.getReferenceFromUrl(audioStory?.url!!)
            Glide.with(this).load(audioStory?.url_image).into(audioStoryImage as ImageView)
            prepareMediaPlayerStreaming()
            prepareButtonDownload()
        }

        audioStoryTitle?.text = audioStory?.title

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

