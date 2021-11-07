package hu.petrik.quizion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import hu.petrik.quizion.elemek.Quiz
import com.example.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Question
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
            val kvizek = Quiz.getAll()
            try{
                if (kvizek!!.isNotEmpty()){
                    feltolt(kvizek[0].getHeader())
                }
                else{
                    feltolt("Nem sikerült csatlakozni, vagy az adatot kinyerni...")
                }
            }catch (e : Exception) {
                feltolt( e.message)
            }
        }
        val questionTolt = launch {
            val kerdesek = Question.getAll()
            try {
                if(kerdesek!!.isNotEmpty()){
                    feltolt(kerdesek[0].getcontent())
                }
                else{
                    feltolt("Nem Sikerült csatlakozni")
                }
            }
            catch (e : Exception){
                feltolt(e.message)
            }
        }
        val anwerTolt = launch {
            val valaszok = Answer.getAll()
            try {
                if(valaszok!!.isNotEmpty()){
                    feltolt(valaszok[0].getcontent())
                }
                else{
                    feltolt("Nem Sikerült csatlakozni")
                }
            }
            catch (e : Exception){
                feltolt(e.message)
            }
        }
        //quizTolt.join()
        //questionTolt.join()
        anwerTolt.join()
        Toast.makeText(activity, "Folyamat lezárult", Toast.LENGTH_SHORT).show()
    }

}