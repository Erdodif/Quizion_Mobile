package com.example.quizion

import com.example.quizion.elemek.Question
import com.example.quizion.elemek.Answer
import com.example.quizion.elemek.Quiz

import com.example.quizion.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import com.example.quizion.SQLConnector
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    fun feltolt(kerdes: String, valasz: List<String>?) {
        runOnUiThread(Runnable {
            bind.textViewKerdes!!.text = kerdes
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)/*
        val thread = Thread(Runnable { APIhivas("method=read&table=quiz") })
        if (!thread.isAlive) {
            thread.start()
        }*/
        val thread = Thread(Runnable { SQLConnector.init() })
        if (!thread.isAlive) {
            thread.start()
        }
    }

}