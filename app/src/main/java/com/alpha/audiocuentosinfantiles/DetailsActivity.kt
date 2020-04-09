package com.alpha.audiocuentosinfantiles

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_details.*
import java.io.File


class DetailsActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private var pause: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val image: ImageView = findViewById(R.id.audiocuento_image)
        val title: TextView = findViewById(R.id.titleDetails)
        val description: TextView = findViewById(R.id.descriptionDetails)
        val duration: TextView = findViewById(R.id.durationDetails)
        val downloadFile: Button = findViewById(R.id.downloadFile)
        val storage = FirebaseStorage.getInstance()


        val audioCuento = intent.getSerializableExtra("AUDIOCUENTO") as? AudioCuento
        title.text = audioCuento?.title
        description.text = audioCuento?.description
        duration.text = audioCuento?.duration

        val storageRef = storage.getReferenceFromUrl(audioCuento?.url!!)

        downloadFile.setOnClickListener{
            downloadFile(storage, audioCuento.url)
        }

        Glide.with(this).load(audioCuento.url_image).into(image)

        // Start the media player
        playBtn.setOnClickListener {
            if (pause) {
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                playBtn.isEnabled = false
                pauseBtn.isEnabled = true
                stopBtn.isEnabled = true
                Toast.makeText(applicationContext, "media playing", Toast.LENGTH_SHORT).show()
            } else {
                storageRef.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                        initializeSeekBar()
                        playBtn.isEnabled = false
                        pauseBtn.isEnabled = true
                        stopBtn.isEnabled = true
                        mediaPlayer.setOnCompletionListener {
                            playBtn.isEnabled = true
                            pauseBtn.isEnabled = false
                            stopBtn.isEnabled = false
                            Toast.makeText(this, "end", Toast.LENGTH_SHORT).show()
                        }
                        it.start()
                    })
                    mediaPlayer.prepareAsync()
                }
            }
        }


        // Pause the media player
        pauseBtn.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                pause = true
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = true
                Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
            }
        }
        // Stop the media player
        stopBtn.setOnClickListener {
            if (mediaPlayer.isPlaying || pause.equals(true)) {
                pause = false
                seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                handler.removeCallbacks(runnable)

                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                tv_pass.text = ""
                tv_due.text = ""
                Toast.makeText(this, "media stop", Toast.LENGTH_SHORT).show()
            }
        }
        // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
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

    // Method to initialize seek bar and audio stats
    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }
}

