package hu.petrik.quizion

import android.app.Activity
import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Quiz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import hu.petrik.quizion.elemek.ViewBuilder
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        loadQuiz(this,bind)
        val id = intent.getIntExtra("id", -1)
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        Log.d("id", id.toString())
    }

    fun loadQuiz(context: Activity,binding: ActivityMainBinding) = runBlocking {
        val anwerTolt = launch {
            val valaszok = Answer.getAll()
            val kerdes = Question.getAll()!![0]
            try {
                if (valaszok!!.isNotEmpty()) {
                    ViewBuilder.kerdesBetolt(context,bind.textViewKerdes!!,kerdes.getcontent())
                    ViewBuilder.valaszBetoltMind(context,bind.layoutValaszok, valaszok)
                } else {
                    ViewBuilder.kerdesBetolt(context,bind.textViewKerdes!!,"Nem Sikerült csatlakozni")
                }
            } catch (e: Exception) {
                ViewBuilder.kerdesBetolt(context,bind.textViewKerdes!!,e.message)
            }
        }
        //quizTolt.join()
        //questionTolt.join()
        anwerTolt.join()
        Toast.makeText(context, "Folyamat lezárult", Toast.LENGTH_SHORT).show()
    }

}