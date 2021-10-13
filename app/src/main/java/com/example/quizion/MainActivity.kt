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
        // TODO: 2021. 10. 13.  
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