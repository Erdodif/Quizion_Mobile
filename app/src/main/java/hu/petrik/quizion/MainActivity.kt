package hu.petrik.quizion

import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Quiz

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.getExtras
import androidx.core.view.*
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    fun toPX(dp: Int): Int {
        Log.d("density", resources.displayMetrics.density.toString())
        return (resources.displayMetrics.density * dp).toInt()
    }

    fun feltolt(kerdes: String?, valasz: List<Answer>? = null) {
        runOnUiThread(Runnable {
            bind.textViewKerdes!!.text = kerdes
        })
        if (valasz !== null) {
            bind.layoutValaszok.removeAllViews()
            for (i in valasz.indices) {
                val valaszGomb = MaterialButton(this)
                val lp = LinearLayout.LayoutParams(bind.layoutValaszok.layoutParams)
                lp.setMargins(20)
                Log.d("ki", valasz.get(i).getcontent()!!)
                (valaszGomb as MaterialButton).apply {
                    isAllCaps = false
                    text = valasz.get(i).getcontent()!!
                    layoutParams = lp
                    setPadding(toPX(15))
                    //margin és padding körbe
                    textSize = 20F
                    setTextColor(getColor(R.color.primary))
                    //background
                    backgroundTintList = getColorStateList(R.color.on_primary)
                    cornerRadius = 20
                }
                bind.layoutValaszok.addView(valaszGomb)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        loadQuiz(this)
        val id = intent.getIntExtra("id",-1)
        if (id == -1){
            return
        }
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        Log.d("id",id.toString())
        /*bind.buttonTempApi!!.setOnClickListener {
            loadQuiz(this)
        }*/
    }

    fun loadQuiz(activity: MainActivity) = runBlocking {
        /*val quizTolt = launch {
            val kvizek = Quiz.getAll()
            try {
                if (kvizek!!.isNotEmpty()) {
                    feltolt(kvizek[0].getHeader())
                } else {
                    feltolt("Nem sikerült csatlakozni, vagy az adatot kinyerni...")
                }
            } catch (e: Exception) {
                feltolt(e.message)
            }
        }
        val questionTolt = launch {
            val kerdesek = Question.getAll()
            try {
                if (kerdesek!!.isNotEmpty()) {
                    feltolt(kerdesek[0].getcontent())
                } else {
                    feltolt("Nem Sikerült csatlakozni")
                }
            } catch (e: Exception) {
                feltolt(e.message)
            }
        }*/
        val anwerTolt = launch {
            val valaszok = Answer.getAll()
            val kerdesek = Question.getAll()
            try {
                if (valaszok!!.isNotEmpty() && kerdesek!!.isNotEmpty()) {
                    feltolt(kerdesek[0].getcontent(), valaszok)
                } else {
                    feltolt("Nem Sikerült csatlakozni")
                }
            } catch (e: Exception) {
                feltolt(e.message)
            }
        }
        //quizTolt.join()
        //questionTolt.join()
        anwerTolt.join()
        Toast.makeText(activity, "Folyamat lezárult", Toast.LENGTH_SHORT).show()
    }

}