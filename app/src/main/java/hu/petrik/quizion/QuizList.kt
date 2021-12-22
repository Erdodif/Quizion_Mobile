package hu.petrik.quizion

import hu.petrik.quizion.databinding.ActivityQuizListBinding
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.elemek.ViewBuilder
import kotlinx.coroutines.runBlocking
import hu.petrik.quizion.elemek.Quiz
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast

class QuizList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        //ideiglenes
        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key),
            Context.MODE_PRIVATE)
        val token = sharedPref.getString("Token","")!!
        kiirat(this, bind.layoutQuizList,token)
        bind.layoutQuizList.removeView(bind.tempLayout)
        setContentView(bind.root)
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
    }

    fun kiirat(context: Activity, hova: LinearLayout, token :String) = runBlocking {
        val quizTolt = launch {
            Log.d("Coroutine állapota", "fut")
            val params: HashMap<String, Any> = HashMap()
            params["active"] = 1
            val kvizek = Quiz.getAllActive()
            try {
                if (kvizek.isNotEmpty()) {
                    ViewBuilder.kvizBetoltMind(context, hova, kvizek,token)
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