package hu.petrik.quizion

import hu.petrik.quizion.databinding.ActivityQuizListBinding
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.elemek.ViewBuilder
import kotlinx.coroutines.runBlocking
import hu.petrik.quizion.elemek.Quiz
import kotlinx.coroutines.launch
import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.util.Log

class QuizList : AppCompatActivity() {
    private lateinit var bind: ActivityQuizListBinding

    fun toPX(dp: Int): Int {
        return (resources.displayMetrics.density * dp).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(bind.root)
        //ideiglenes
        kiirat(this,bind)
    }

    fun kiirat(context: Activity,bind:ActivityQuizListBinding) = runBlocking {
        val quizTolt = launch {
            Log.d("Coroutine állapota", "fut")
            val kvizek = Quiz.getAll()
            try {
                if (kvizek!!.isNotEmpty()) {
                    ViewBuilder.kvizBetoltMind(context, bind.layoutQuizList, kvizek)
                } else {
                    ViewBuilder.labelHibaBetolt(
                        context, bind.layoutQuizList,
                        "Nem sikerült csatlakozni, vagy az adatot kinyerni..."
                    )
                }
            } catch (e: Exception) {
                ViewBuilder.labelHibaBetolt(context, bind.layoutQuizList, e.message.toString())
            }
        }
        quizTolt.join()
    }

}