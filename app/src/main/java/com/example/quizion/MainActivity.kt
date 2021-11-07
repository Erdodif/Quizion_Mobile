package com.example.quizion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizion.elemek.Quiz
import com.example.quizion.databinding.ActivityMainBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.*

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
        loadQuiz(this)
        bind.buttonTempApi!!.setOnClickListener {
            loadQuiz(this)
        }
    }

    fun loadQuiz(activity: MainActivity) = runBlocking{
        val quizTolt = launch {
            val quizek = Quiz.getQuizAll()
            try{
                if (quizek!!.isNotEmpty()){
                    feltolt(quizek[0].getHeader())
                }
                else{
                    feltolt("Nem sikerült csatlakozni, vagy az adatot kinyerni...")
                }
            }catch (e : Exception) {
                feltolt( e.message)
            }

        }
        quizTolt.join()
        Toast.makeText(activity, "Folyamat lezárult", Toast.LENGTH_SHORT).show()
    }

}