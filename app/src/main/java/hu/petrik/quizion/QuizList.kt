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

@Suppress("MemberVisibilityCanBePrivate")
class QuizList : AppCompatActivity() {

    @Suppress("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val token = sharedPref.getString("Token", "")!!
        loadQuizzes(this, bind.layoutQuizList, token)
        bind.layoutQuizList.removeView(bind.tempLayout)
        setContentView(bind.root)
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
    }

    fun loadQuizzes(context: Activity, quizLayout: LinearLayout, token: String): Unit = runBlocking {
        launch {
            Log.d("Coroutine state", "running")
            val params: HashMap<String, Any> = HashMap()
            params["active"] = 1
            val quizzes = Quiz.getAllActive()
            try {
                if (quizzes.isNotEmpty()) {
                    ViewBuilder.loadQuizAll(context, quizLayout, quizzes, token)
                } else {
                    ViewBuilder.loadLabelError(
                        context, quizLayout,
                        getString(R.string.server_connection_error)
                    )
                }
            } catch (e: Exception) {
                ViewBuilder.loadLabelError(context, quizLayout, e.message.toString())
            }
        }.join()
    }

}