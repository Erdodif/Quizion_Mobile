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
import hu.petrik.quizion.elemek.ViewBuilder.Companion.kerdesBetolt

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        val id = intent.getIntExtra("id", -1)
        try {
            Log.d("id", id.toString())
            loadQuiz(bind, id)
        } catch (e: Exception) {
            kerdesBetolt(bind.textViewKerdes!!, e.toString())
        }
    }

    fun loadQuiz(binding: ActivityMainBinding, id: Int, index: Int = 1) = runBlocking {
        val anwerTolt = launch {
            val kerdes = Question.getByQuiz(id, index)
            val valaszok = Answer.getAllByQuestion(id, index)
            Log.d("valaszok", valaszok.toString())
            try {
                if (valaszok.isNotEmpty()) {
                    kerdesBetolt(binding.textViewKerdes!!, kerdes.content)
                    ViewBuilder.valaszBetoltMind(
                        binding.root.context as Activity,
                        binding.layoutValaszok,
                        valaszok
                    )
                } else {
                    kerdesBetolt(
                        binding.textViewKerdes!!,
                        "Nem Sikerült csatlakozni"
                    )
                }
            } catch (e: Exception) {
                kerdesBetolt(bind.textViewKerdes!!, e.message)
            }
        }
        anwerTolt.join()
        Toast.makeText(binding.root.context, "Folyamat lezárult", Toast.LENGTH_SHORT).show()
    }
}