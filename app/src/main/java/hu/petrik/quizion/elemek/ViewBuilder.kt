package hu.petrik.quizion.elemek

import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Quiz

import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.MainActivity
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Intent
import android.app.Activity
import hu.petrik.quizion.R
import android.view.View
import android.util.Log

class ViewBuilder {
    companion object{
        fun toPX(context :Activity,dp: Int): Int {
            return (context.resources.displayMetrics.density * dp).toInt()
        }

        fun kerdesBetolt(context:Activity,kerdes_helye :TextView,kerdes: String?){
            context.runOnUiThread(Runnable {
                kerdes_helye.text = kerdes
            })
        }

        fun valaszBetoltMind(context:Activity,valaszok_helye : LinearLayout, valasz: List<Answer>? = null) {
            context.runOnUiThread(Runnable {
                if (valasz !== null) {
                    valaszok_helye.removeAllViews()
                    for (i in valasz.indices) {
                        val valaszGomb = MaterialButton(context)
                        val lp = LinearLayout.LayoutParams(valaszok_helye.layoutParams)
                        lp.setMargins(20)
                        Log.d("ki", valasz.get(i).getcontent()!!)
                        (valaszGomb as MaterialButton).apply {
                            isAllCaps = false
                            text = valasz.get(i).getcontent()!!
                            layoutParams = lp
                            setPadding(toPX(context,15))
                            //margin és padding körbe
                            textSize = 20F
                            setTextColor(context.getColor(R.color.primary))
                            //background
                            backgroundTintList = context.getColorStateList(R.color.on_primary)
                            cornerRadius = 20
                        }
                        valaszok_helye.addView(valaszGomb)
                    }
                }
            })
        }

        fun kvizBetoltMind(context: Activity,kvizek_helye : LinearLayout, tartalom: List<Quiz>) {
            for (elem in tartalom) {
                Log.d("Ciklikus hókonvágás", "jelen")
                val lp = LinearLayout.LayoutParams(kvizek_helye.layoutParams)
                lp.setMargins(20, 10, 20, 10)
                val layout = LinearLayout(context)
                layout.apply {
                    setPadding(toPX(context,15))
                    layoutParams = lp
                }
                val header = TextView(context)
                lp.setMargins(20, 0, 20, 0)
                header.apply {
                    layoutParams = lp
                    text = elem.getHeader()
                    isAllCaps = false
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
                val description = TextView(context)
                lp.setMargins(20, 10, 20, 10)
                description.apply {
                    layoutParams = lp
                    text = elem.getDescription()
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
                val indito = MaterialButton(context)
                lp.setMargins(20, 0, 20, 0)
                indito.apply {
                    isAllCaps = false
                    text = "Játék"
                    layoutParams = lp
                    setPadding(toPX(context,15))
                    textSize = 20F
                    setTextColor(context.getColor(R.color.on_primary))
                    backgroundTintList = context.getColorStateList(R.color.secondary_variant)
                    cornerRadius = 20
                }
                layout.addView(header)
                layout.addView(description)
                layout.addView(indito)
                indito.setOnClickListener {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("id", elem.getId())
                    context.startActivity(intent)
                    context.finish()
                }
                layout.measure(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                kvizek_helye.addView(layout)
                Log.d("Fejléc", elem.getHeader())
                Log.d("Leírás", elem.getDescription())
                Log.d("Indító", elem.getId().toString())
            }
            kvizek_helye.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        fun labelHibaBetolt(context: Activity,kvizek_helye : LinearLayout, hiba:String) {
            context.runOnUiThread {
                kvizek_helye.removeAllViews()
                val layout = LinearLayout(context)
                layout.apply {
                    setPadding(toPX(context,15))
                    backgroundTintList = context.getColorStateList(R.color.on_primary)
                }
                val header = TextView(context)
                val lp = LinearLayout.LayoutParams(kvizek_helye.layoutParams)
                lp.setMargins(20, 0, 20, 0)
                header.apply {
                    layoutParams = lp
                    text = "Hiba"
                    isAllCaps = false
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
                val description = TextView(context)
                lp.setMargins(20, 10, 20, 10)
                description.apply {
                    layoutParams = lp
                    text = hiba
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
                kvizek_helye.addView(layout, lp)
                layout.addView(header)
                layout.addView(description)
            }
        }

    }
}