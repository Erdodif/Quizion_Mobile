package com.example.quizion

import com.example.quizion.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import com.example.quizion.adatbazis.SQLConnector
import android.os.Bundle
import com.example.quizion.elemek.Quiz

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    fun feltolt(kerdes: String?, valasz: List<String>? = null) {
        runOnUiThread(Runnable {
            bind.textViewKerdes!!.text = kerdes
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        val thread = Thread(Runnable{
            val quizek = Quiz.getQuizAll()
            if (quizek!!.size > 0){
                feltolt(quizek.get(0).getHeader())
            }
            else{
                feltolt("Nem sikerült csatlakozni, vagy az adatot kinyerni...")
            }
        })
        thread.start()
    }

}