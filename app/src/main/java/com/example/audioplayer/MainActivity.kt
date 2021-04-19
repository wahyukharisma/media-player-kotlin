package com.example.audioplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.util.Constants

class MainActivity : AppCompatActivity(), BaseMediaPlayer {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer

    private var isPause = false
    private var isEnd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initialize()

        with(binding) {
            ibPlayPause.setOnClickListener {
                if (isPause) {
                    if(isEnd){
                        initialize()
                    }else{
                        play()
                    }
                } else {
                    pause()
                }
            }

            ibBackward.setOnClickListener { backward() }
            ibForward.setOnClickListener { forward() }

            sbAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mediaPlayer.seekTo(progress * 1000)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun initializeSeekBar() {
        with(binding) {
            sbAudio.max = mediaPlayer.seconds

            val handler = Handler(Looper.getMainLooper())

            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        sbAudio.progress = mediaPlayer.currentSeconds
                        tvRunningTime.text = getTimeString(mediaPlayer.currentPosition)

                        if(mediaPlayer.currentPosition >= mediaPlayer.duration){
                            mediaPlayer.stop()
                            isPlayButton(true)
                            isEnd = true
                        }

                        handler.postDelayed(this, 1000)
                    } catch (ex: Exception) {
                        sbAudio.progress = 0
                    }
                }
            }, 0)
        }
    }

    private fun initializeTextDuration(){
        with(binding){
            tvMaxTime.text = getTimeString(mediaPlayer.duration)
            tvRunningTime.text = getTimeString(mediaPlayer.currentPosition)
        }
    }

    private fun isPlayButton(value: Boolean){
        if(value){
            binding.ibPlayPause.setImageResource(R.drawable.ic_play)
        }else{
            binding.ibPlayPause.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun initialize() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(Constants.MEDIA_URL_3)
                prepare()
                start()
            }
            initializeSeekBar()
            initializeTextDuration()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun release() {
        isPause = true
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    override fun play() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition)
        mediaPlayer.start()
        isPause = false
        isPlayButton(false)
    }

    override fun pause() {
        mediaPlayer.pause()
        isPause = true
        isPlayButton(true)
    }

    override fun forward() {
        try {
            val current = mediaPlayer.currentPosition
            mediaPlayer.seekTo(current + 5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun backward() {
        try {
            val current = mediaPlayer.currentPosition
            mediaPlayer.seekTo(current - 5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTimeString(millis: Int): String {
        val buf = StringBuffer()
        val minutes = (millis % (1000 * 60 * 60) / (1000 * 60))
        val seconds = (millis % (1000 * 60 * 60) % (1000 * 60) / 1000)
        buf
            .append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))
        return buf.toString()
    }

    // Extension property to get media player duration in seconds
    private val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }


    // Extension property to get media player current position in seconds
    private val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }
}