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
import android.widget.LinearLayout

class QuizList : AppCompatActivity() {
    private lateinit var bind: ActivityQuizListBinding

    fun toPX(dp: Int): Int {
        return (resources.displayMetrics.density * dp).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        //ideiglenes
        kiirat(this,bind.layoutQuizList)
        bind.layoutQuizList.removeView(bind.tempLayout)
        setContentView(bind.root)
    }

    fun kiirat(context: Activity,hova :LinearLayout) = runBlocking {
        val quizTolt = launch {
            Log.d("Coroutine állapota", "fut")
            val kvizek = Quiz.getAll()
            try {
                if (kvizek!!.isNotEmpty()) {
                    ViewBuilder.kvizBetoltMind(context, hova, kvizek)
                } else {
                    ViewBuilder.labelHibaBetolt(
                        context, hova,
                        "Nem sikerült csatlakozni, vagy az adatot kinyerni..."
                    )
                }
            } catch (e: Exception) {
                ViewBuilder.labelHibaBetolt(context, hova, e.message.toString())
            }
        }
        quizTolt.join()
    }

}