package com.example.quizion

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.example.quizion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        APIhivas(bind)
    }

    fun APIhivas(bind: ActivityMainBinding) {
        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        val url = "https://10.147.20.1/quizion/adatok/index.php?method=read&table=quiz";

        // Formulate the request and handle the response.

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val ki = "Response is: " + response.substring(0, 500);
                bind.textViewKerdes?.setText(ki)
                Log.d("Eredmény:", ki)

            },
            { error ->
                val ki = "Ez baj, $error"
                bind.textViewKerdes?.setText(ki)
                Log.d("Hiba: ", ki)
            }
        )
        requestQueue.add(stringRequest)
    }

}