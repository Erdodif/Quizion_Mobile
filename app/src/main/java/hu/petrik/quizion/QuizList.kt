package hu.petrik.quizion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.widget.LinearLayout
import android.widget.TextView
import hu.petrik.quizion.elemek.Quiz
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.databinding.ActivityQuizListBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QuizList : AppCompatActivity() {
    private lateinit var bind: ActivityQuizListBinding

    fun toPX(dp: Int): Int {
        Log.d("density", resources.displayMetrics.density.toString())
        return (resources.displayMetrics.density * dp).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(bind.root)
        //ideiglenes
        kiirat(bind)
    }

    fun kiirat(context: ActivityQuizListBinding) = runBlocking{
        val quizTolt = launch{
            val kvizek = Quiz.getAll()
            try {
                if (kvizek!!.isNotEmpty()) {
                    labelBetolt(context,kvizek)
                } else {
                    labelBetolt(context,"Nem sikerült csatlakozni, vagy az adatot kinyerni...")
                }
            } catch (e: Exception) {
                labelBetolt(context,e.message.toString())
            }
        }
        quizTolt.join()
    }
    fun labelBetolt(context : ActivityQuizListBinding,tartalom :List<Quiz>){
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        bind.layoutQuizList.removeAllViews()
        for (i in tartalom.indices){
            val layout = LinearLayout(this)
            layout.apply {
                setPadding(toPX(15))
            }
            val header = TextView(this)
            val lp = LinearLayout.LayoutParams(bind.layoutQuizList.layoutParams)
            lp.setMargins(20,0,20,0)
            header.apply {
                layoutParams = lp
                text = tartalom[i].getHeader()
                isAllCaps = false
                textSize = 20F
                setTextColor(getColor(R.color.primary_variant))
                textAlignment = TEXT_ALIGNMENT_CENTER
            }
            val description = TextView(this)
            lp.setMargins(20,10,20,10)
            description.apply {
                layoutParams = lp
                text = tartalom[i].getDescription()
                textSize = 20F
                setTextColor(getColor(R.color.primary_variant))
                textAlignment = TEXT_ALIGNMENT_TEXT_START
            }
            val indito = MaterialButton(this)
            lp.setMargins(20,0,20,0)
            indito.apply {
                isAllCaps = false
                text = "Játék"
                layoutParams = lp
                setPadding(toPX(15))
                textSize = 20F
                setTextColor(getColor(R.color.on_primary))
                backgroundTintList = getColorStateList(R.color.secondary_variant)
                cornerRadius = 20
            }
            indito.setOnClickListener {
                atteres(tartalom[i].getId())
            }
            layout.addView(header)
            layout.addView(description)
            layout.addView(indito)
            bind.layoutQuizList.addView(layout)

        }

    }
    fun labelBetolt(context : ActivityQuizListBinding,tartalom : String){
        bind.layoutQuizList.removeAllViews()
        for (i in tartalom.indices){
            val layout = LinearLayout(this)
            layout.apply {
                setPadding(toPX(15))
            }
            val header = TextView(this)
            val lp = LinearLayout.LayoutParams(bind.layoutQuizList.layoutParams)
            lp.setMargins(20,0,20,0)
            header.apply {
                layoutParams = lp
                text = "Hiba"
                isAllCaps = false
                textSize = 20F
                setTextColor(getColor(R.color.primary_variant))
                textAlignment = TEXT_ALIGNMENT_CENTER
            }
            val description = TextView(this)
            lp.setMargins(20,10,20,10)
            description.apply {
                layoutParams = lp
                text = tartalom
                textSize = 20F
                setTextColor(getColor(R.color.primary_variant))
                textAlignment = TEXT_ALIGNMENT_TEXT_START
            }
            val indito = MaterialButton(this)
            lp.setMargins(20,0,20,0)
            indito.apply {
                isAllCaps = false
                text = "Játék"
                layoutParams = lp
                setPadding(toPX(15))
                textSize = 20F
                setTextColor(getColor(R.color.on_primary))
                backgroundTintList = getColorStateList(R.color.secondary_variant)
                cornerRadius = 20
            }
            indito.setOnClickListener {
                atteres(0)
            }
            layout.addView(header)
            layout.addView(description)
            layout.addView(indito)
            bind.layoutQuizList.addView(layout)

        }

    }

    fun atteres(id:Int){
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
        finish()
    }
}