package com.example.quizion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.quizion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    fun APIhivas(){
        // ...

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://10.147.20.1/quizion/adatok/index.php?method=listaz&tabla=quiz";

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                // Display the first 500 characters of the response string.
                Toast.makeText(this,
                    "Response is: ${response.substring(0, 500)}", Toast.LENGTH_SHORT).show()
            },
            { Toast.makeText(this,
                "Ez baj", Toast.LENGTH_SHORT).show() })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind : ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        bind.buttonTempApi?.setOnLongClickListener{
            APIhivas()
            true
        }
    }
}