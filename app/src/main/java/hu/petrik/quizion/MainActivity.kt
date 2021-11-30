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
        loadQuiz(this,bind,id)
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        Log.d("id", id.toString())
    }

    fun loadQuiz(context: Activity,binding: ActivityMainBinding,id : Int,start :Int = 0) = runBlocking {
        val anwerTolt = launch {
            val kerdes = Question.getAll()[start]
            val valaszok = Answer.getAll()
            var kerdesreValaszok :List<Answer> = ArrayList()
            Log.d("valaszok",valaszok.toString())
            for(i in valaszok.indices){
                if(valaszok[i].questionId == kerdes.id){
                    kerdesreValaszok = kerdesreValaszok.plus(valaszok[i])
                }
            }
            try {
                if (kerdesreValaszok.isNotEmpty()) {
                    kerdesBetolt(bind.textViewKerdes!!, kerdes.content)
                    ViewBuilder.valaszBetoltMind(context, bind.layoutValaszok, kerdesreValaszok)
                } else {
                    kerdesBetolt(
                        bind.textViewKerdes!!,
                        "Nem Sikerült csatlakozni"
                    )
                }
            } catch (e: Exception) {
                kerdesBetolt(bind.textViewKerdes!!, e.message)
            }
        }
        //quizTolt.join()
        //questionTolt.join()
        anwerTolt.join()
        Toast.makeText(context, "Folyamat lezárult", Toast.LENGTH_SHORT).show()
    }

}