package com.example.audioplayer

interface BaseMediaPlayer {
    fun initialize()
    fun release()
    fun play()
    fun pause()
    fun forward()
    fun backward()
}