package com.example.quizion

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.example.quizion.databinding.ActivityMainBinding
import org.jetbrains.annotations.NotNull
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var bind : ActivityMainBinding
    private var threadLocked = false;

    fun szovegModosit(tartalom : String){
        runOnUiThread(Runnable {
            bind.textViewKerdes!!.text = tartalom
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        bind.buttonTempApi!!.setOnClickListener{
            val hivas = Thread(Runnable{APIhivas()})
            if(!hivas.isAlive){
                hivas.start()
            }

        }
    }


    fun APIhivas() {
        if (threadLocked){
            Log.d("Figyelmeztetés","Szál foglalva!")
            return
        }
        threadLocked = true;
        Log.d("Mellékszál állapota","fut")
        val baseUrl = "http://10.147.20.1/adatok/index.php"
        val quizkeres = "?method=read&table=quiz"
        val connection = URL(baseUrl + quizkeres).openConnection() as HttpURLConnection
        try {
            val data = connection.inputStream.bufferedReader().readText()
            szovegModosit(data)
            Log.d("Eredmény:", data)
            // ... do something with "data"
        } catch (e :Exception){
            Log.d("Hiba",e.message.toString())
            szovegModosit(e.message.toString())
        }
        finally {
            connection.disconnect()
            threadLocked = false;
        }
    }

}