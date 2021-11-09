package hu.petrik.quizion

import android.os.Bundle
import android.content.Intent
import hu.petrik.quizion.elemek.Quiz
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.databinding.ActivityQuizListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QuizList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(bind.root)
        //ideiglenes
        /*val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()*/
        kiirat()
    }

    fun kiirat() = runBlocking{
        val quizTolt = launch{
            val kvizek = Quiz.getAll()
            try {
                if (kvizek!!.isNotEmpty()) {
                    gombBetolt(kvizek)
                } else {
                    gombBetolt("Nem sikerült csatlakozni, vagy az adatot kinyerni...")
                }
            } catch (e: Exception) {
                gombBetolt(e.message.toString())
            }
        }
    }
    fun gombBetolt(tartalom :List<Quiz>){

    }
    fun gombBetolt(tartalom : String){

    }
}