package com.rr.baselab2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile


class MainActivity : AppCompatActivity() {

    private lateinit var buttonPlay: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonPrev: Button
    private lateinit var buttonNext: Button
    private lateinit var name: TextView
    private lateinit var lvDatos : ListView

    var mediaPlayer = MediaPlayer()
    var vector = ArrayList<Uri>()
    var vectorName = ArrayList<String>()
    var currentIndex = 0
    var arrayAdapter: ArrayAdapter<*>? = null

    var lista: MutableList<String>? = null
    var listaAutor: MutableList<String>? = null
    var listaAlbum: MutableList<String>? = null
    val m_metaRetriever = android.media.MediaMetadataRetriever()


    companion object{ var OPEN_DIRECTORY_REQUEST_CODE=1 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonPlay = findViewById(R.id.buttonPlay)
        buttonPause = findViewById(R.id.buttonPause)
        buttonPrev = findViewById(R.id.buttonPrev)
        buttonNext = findViewById(R.id.buttonNext)
        name = findViewById(R.id.textView)

        lista = mutableListOf()
        lvDatos = findViewById<ListView>(R.id.lvDatos)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista!!)
        lvDatos.adapter = arrayAdapter

        setOnClickListeners(this)
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)

        lvDatos = findViewById<View>(R.id.lvDatos) as ListView
        lvDatos.isClickable = true
        lvDatos.onItemClickListener =
            OnItemClickListener { arg0, arg1, position, arg3 ->
                    currentIndex = position
                    mediaPlayer.stop()
                    mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(this, vector[position])
                    name.text = vectorName[position]
                    mediaPlayer.prepare();
                    mediaPlayer.start()
            }
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
                                    m_metaRetriever.setDataSource(this, file.uri)

                                    lista?.add( "Titulo: " + m_metaRetriever.extractMetadata(
                                         MediaMetadataRetriever.METADATA_KEY_TITLE).toString() +
                                            "\nArtista: "+ m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
                                        + "\nAlbum: " + m_metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM).toString()
                                    )

                                }
                            }
                        }catch (e: Exception){
                            Log.e("Error","Hakunamatata")
                        }
                    }
                arrayAdapter?.notifyDataSetChanged()
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
            }else {
                Toast.makeText(context, "No hay mas musica que reproducir...", Toast.LENGTH_SHORT).show()
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
            }else {
                Toast.makeText(context, "No hay mas musica que reproducir...", Toast.LENGTH_SHORT).show()
            }
        }

    }
}