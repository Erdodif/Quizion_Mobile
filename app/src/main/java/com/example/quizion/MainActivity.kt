package com.example.quizion

import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.example.quizion.databinding.ActivityMainBinding
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private var threadLocked = false
    private var currentData: String? = ""

    fun feltolt(kerdes: String, valasz: List<String>) {
        runOnUiThread(Runnable {
            //bind.textViewKerdes!!.text = tartalom
        })
    }

    fun init() {
        val quiz = //Json.encodeToString
            APIhivas("method=read&table=quiz")
        runOnUiThread(Runnable {
            bind.textViewKerdes!!.text = quiz
        })
    }

    fun APIhivas(params: String): String? {
        if (threadLocked) {
            Log.d("Figyelmeztetés", "Szál foglalva!")
            return null
        }
        threadLocked = true;
        Log.d("Mellékszál állapota", "fut")
        val baseUrl = "http://10.147.20.1/adatok/index.php?"
        val connection = URL(baseUrl + params).openConnection() as HttpURLConnection
        var kiad: String? = ""
        try {
            currentData = null
            val data = connection.inputStream.bufferedReader().readText()
            Log.d("Kérés állapota:", "Adat visszatért!")
            kiad = data
        } catch (e: Exception) {
            Log.d("Hiba", e.message.toString())
            kiad = null
        } finally {
            connection.disconnect()
            threadLocked = false;
        }
        return kiad
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        val thread = Thread(Runnable { APIhivas("method=read&table=quiz") })
        if (!thread.isAlive) {
            thread.start()
        }
    }


}