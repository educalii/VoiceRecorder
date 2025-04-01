package com.example.voicerecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var isRecording = false
    private var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recordBtn = findViewById<Button>(R.id.recordBtn)
        val playBtn = findViewById<Button>(R.id.playBtn)

        recordBtn.setOnClickListener {
            if (!isRecording) startRecording() else stopRecording()
        }

        playBtn.setOnClickListener {
            playRecording()
        }
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(getRecordingPath())
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                start()
                isRecording = true
                findViewById<Button>(R.id.recordBtn).text = "Stop"
                Toast.makeText(this@MainActivity, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        findViewById<Button>(R.id.recordBtn).text = "Record"
        Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show()
    }

    private fun playRecording() {
        if (filePath.isEmpty()) {
            Toast.makeText(this, "No recording to play", Toast.LENGTH_SHORT).show()
            return
        }

        player = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
            Toast.makeText(this@MainActivity, "Playing...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRecordingPath(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Recording_$timeStamp.3gp"
        filePath = "${getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/$fileName"
        return filePath
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        player?.release()
    }
}
