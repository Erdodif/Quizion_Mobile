package hu.petrik.quizion.components

import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.activities.MainActivity
import androidx.core.view.setPadding
import android.widget.LinearLayout
import android.widget.TextView
import android.app.Activity
import android.util.Log

class ViewBuilder {
    companion object {
        @Suppress("MemberCouldBePrivate")
        fun toPX(context: Activity, dp: Int): Int {
            return (context.resources.displayMetrics.density * dp).toInt()
        }

        fun loadQuestion(questionView: TextView, question: String?) {
            (questionView.context as Activity).runOnUiThread {
                questionView.text = question
            }
        }

        private fun putAnswerButton(answerLayout: LinearLayout, answer: Answer): Int {
            val context = answerLayout.context as Activity
            val answerButton = MaterialButton(context)
            val lp = LinearLayout.LayoutParams(answerLayout.layoutParams)
            val viewId = MaterialButton.generateViewId()
            lp.setMargins(toPX(context, 9), toPX(context, 2), toPX(context, 9), toPX(context, 2))
            Log.d("ki", answer.content)
            Log.d("ki id", viewId.toString())
            answerButton.apply {
                isAllCaps = false
                text = answer.content
                layoutParams = lp
                id = viewId
                setPadding(toPX(context, 15))
                textSize = 20F
                cornerRadius = toPX(context, 10)
            }
            answerLayout.addView(answerButton)
            (answerLayout.context as MainActivity).setAnswerState(viewId, AnswerState.DEFAULT)
            return viewId
        }

        fun loadAnswerAll(
            context: MainActivity,
            answerLayout: LinearLayout,
            answer: List<Answer>? = null
        ): ArrayList<Int> {
            val ids = ArrayList<Int>()
            context.runOnUiThread {
                if (answer !== null) {
                    answerLayout.removeAllViewsInLayout()
                    for (i in answer.indices) {
                        ids.add(putAnswerButton(answerLayout, answer[i]))
                    }
                }
            }
            Log.d("ids", ids.toString())
            return ids
        }
    }
}