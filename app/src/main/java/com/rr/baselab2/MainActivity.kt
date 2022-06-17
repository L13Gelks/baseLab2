package com.rr.baselab2

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.lang.Exception
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var buttonPlay: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonPrev: Button
    private lateinit var buttonNext: Button
    private lateinit var name: TextView

    var mediaPlayer = MediaPlayer()
    var vector = ArrayList<Uri>()
    var vectorName = ArrayList<String>()
    var currentIndex = 0


    companion object{ var OPEN_DIRECTORY_REQUEST_CODE=1 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonPlay = findViewById(R.id.buttonPlay)
        buttonPause = findViewById(R.id.buttonPause)
        buttonPrev = findViewById(R.id.buttonPrev)
        buttonNext = findViewById(R.id.buttonNext)
        name = findViewById(R.id.textView)

        setOnClickListeners(this)
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OPEN_DIRECTORY_REQUEST_CODE)
            if(resultCode == Activity.RESULT_OK) {
                var directoryUri = data?.data ?: return
                Log.e("Directorio", directoryUri.toString())
                var rootTree = DocumentFile.fromTreeUri(this, directoryUri)

                    for (file in rootTree!!.listFiles()) {
                        try {
                            file.name?.let {
                                if(it.endsWith("mp3")){
                                    vector.add(file.uri)
                                    vectorName.add(it)
                                }
                            }
                        }catch (e: Exception){
                            Log.e("Error","Hakunamatata")
                        }

                    }

                name.text = vectorName[0]
                mediaPlayer.setDataSource(this, vector[0])
                mediaPlayer.prepare();
                mediaPlayer.start()
            }
    }

    private fun setOnClickListeners(context: Context) {
        buttonPlay.setOnClickListener {
            mediaPlayer.start()
            Toast.makeText(context, "Reproduciendo...", Toast.LENGTH_SHORT).show()
        }

        buttonPause.setOnClickListener {
            mediaPlayer.pause()
            Toast.makeText(context, "En pausa...", Toast.LENGTH_SHORT).show()
        }

        buttonNext.setOnClickListener {
            if(currentIndex < vector.size-1){
                currentIndex++
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(this, vector[currentIndex])
                name.text = vectorName[currentIndex]
                mediaPlayer.prepare();
                mediaPlayer.start()
            }
        }

        buttonPrev.setOnClickListener {
            if(currentIndex > 0){
                currentIndex--
                mediaPlayer.stop()
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(this, vector[currentIndex])
                name.text = vectorName[currentIndex]
                mediaPlayer.prepare();
                mediaPlayer.start()
            }
        }

    }
}