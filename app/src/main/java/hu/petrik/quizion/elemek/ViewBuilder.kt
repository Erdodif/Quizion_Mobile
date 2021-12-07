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
import android.graphics.drawable.Drawable
import hu.petrik.quizion.R
import android.view.View
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources

class ViewBuilder {
    companion object {
        fun toPX(context: Activity, dp: Int): Int {
            return (context.resources.displayMetrics.density * dp).toInt()
        }

        fun kerdesBetolt(kerdes_helye: TextView, kerdes: String?) {
            (kerdes_helye.context as Activity).runOnUiThread {
                kerdes_helye.text = kerdes
            }
        }

        fun valaszGombKreal(valaszok_helye: LinearLayout, valasz: Answer) {
            val context = valaszok_helye.context as Activity
            val valaszGomb = MaterialButton(context)
            val lp = LinearLayout.LayoutParams(valaszok_helye.layoutParams)
            lp.setMargins(toPX(context, 9), toPX(context, 2), toPX(context, 9), toPX(context, 2))
            Log.d("ki", valasz.content)
            valaszGomb.apply {
                isAllCaps = false
                text = valasz.content
                layoutParams = lp
                setPadding(toPX(context, 15))
                textSize = 20F
                setTextColor(context.getColor(R.color.primary))
                backgroundTintList = context.getColorStateList(R.color.on_primary)
                cornerRadius = toPX(context, 10)
                setOnClickListener{
                    (context as MainActivity).jumpOnNext(valasz.id!!)
                }
            }
            valaszok_helye.addView(valaszGomb)
        }

        fun valaszBetoltMind(
            context: Activity,
            valaszok_helye: LinearLayout,
            valasz: List<Answer>? = null
        ) {
            context.runOnUiThread(Runnable {
                if (valasz !== null) {
                    for (i in valasz.indices) {
                        valaszGombKreal(valaszok_helye, valasz[i])
                    }
                }
            })
        }

        fun kvizBetoltMind(context: Activity, kvizek_helye: LinearLayout, tartalom: List<Quiz>) {
            for (elem in tartalom) {
                val lp = LinearLayout.LayoutParams(kvizek_helye.layoutParams)
                lp.setMargins(20, 10, 20, 10)
                val layout = LinearLayout(context)
                layout.apply {
                    setPadding(toPX(context, 15))
                    layoutParams = lp
                    background = AppCompatResources.getDrawable(context, R.color.on_primary)
                    orientation = LinearLayout.VERTICAL
                }
                val header = TextView(context)
                lp.setMargins(10)
                header.apply {
                    layoutParams = lp
                    text = elem.header
                    isAllCaps = false
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
                val description = TextView(context)
                lp.width = LinearLayout.LayoutParams.MATCH_PARENT
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
                description.apply {
                    layoutParams = lp
                    text = elem.description
                    isAllCaps = false
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                }
                val indito = MaterialButton(context)
                lp.setMargins(20, 0, 20, 0)
                indito.apply {
                    isAllCaps = false
                    text = "Játék"
                    layoutParams = lp
                    setPadding(toPX(context, 15))
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
                    intent.putExtra("id", elem.id)
                    context.startActivity(intent)
                    //context.finish()
                }
                layout.measure(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                kvizek_helye.addView(layout)
                Log.d("Fejléc", elem.header)
                Log.d("Leírás", elem.description)
                Log.d("Leírás beiktatva", description.text.toString())
                Log.d("Indító", elem.id.toString())
            }
            kvizek_helye.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        fun labelHibaBetolt(context: Activity, kvizek_helye: LinearLayout, hiba: String) {
            context.runOnUiThread {
                kvizek_helye.removeAllViews()
                val layout = LinearLayout(context)
                val lp = LinearLayout.LayoutParams(kvizek_helye.layoutParams)
                lp.setMargins(20, 10, 20, 10)
                layout.apply {
                    setPadding(toPX(context, 15))
                    layoutParams = lp
                    background = AppCompatResources.getDrawable(context, R.color.on_primary)
                    orientation = LinearLayout.VERTICAL
                }
                val header = TextView(context)
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