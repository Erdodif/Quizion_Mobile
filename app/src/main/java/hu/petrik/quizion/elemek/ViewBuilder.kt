package hu.petrik.quizion.elemek

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
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.marginStart
import hu.petrik.quizion.LeaderboardActivity
import hu.petrik.quizion.QuizList
import hu.petrik.quizion.adatbazis.SQLConnector
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

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

        fun loadQuizAll(
            context: QuizList,
            quitLayout: LinearLayout,
            content: List<Quiz>,
            token: String
        ) {
            for (elem in content) {
                val lp = LinearLayout.LayoutParams(quitLayout.layoutParams)
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
                val playButton = MaterialButton(context)
                lp.setMargins(20, 0, 20, 0)
                playButton.apply {
                    isAllCaps = false
                    text = resources.getText(R.string.play)
                    layoutParams = lp
                    setPadding(toPX(context, 15))
                    textSize = 20F
                    setTextColor(context.getColor(R.color.on_primary))
                    backgroundTintList = context.getColorStateList(R.color.secondary_variant)
                    cornerRadius = 20
                }
                layout.addView(header)
                layout.addView(description)
                layout.addView(playButton)
                playButton.setOnClickListener {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("id", elem.id)
                    intent.putExtra("token", token)
                    context.startActivity(intent)
                    context.finish()
                }
                layout.measure(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                quitLayout.addView(layout)
                Log.d(context.getString(R.string.header), elem.header)
                Log.d(context.getString(R.string.description), elem.description)
            }
            quitLayout.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        fun loadLabelError(context: QuizList, quizLayout: LinearLayout, error: String) {
            context.runOnUiThread {
                quizLayout.removeAllViews()
                val layout = LinearLayout(context)
                val lp = LinearLayout.LayoutParams(quizLayout.layoutParams)
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
                    text = context.getString(R.string.error)
                    isAllCaps = false
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
                val description = TextView(context)
                lp.setMargins(20, 10, 20, 10)
                description.apply {
                    layoutParams = lp
                    text = error
                    textSize = 20F
                    setTextColor(context.getColor(R.color.primary_variant))
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
                quizLayout.addView(layout, lp)
                layout.addView(header)
                layout.addView(description)
            }
        }

        private fun addResultTextView(
            context: LeaderboardActivity,
            to:RelativeLayout,
            content: String,
            vararg rules: Int,
            paramTextSize: Float? = null,
            @ColorRes paramTextColor: Int? = null
        ){
            val textView = TextView(context)
            to.addView(textView)
            val params = textView.layoutParams as RelativeLayout.LayoutParams
            with(params) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                for (i in rules.indices) {
                    addRule(rules[i], 1)
                }
                marginStart = toPX(context, 15)
                marginEnd = toPX(context, 15)
            }
            with(textView) {
                if (paramTextColor === null) {
                    setTextColor(getColor(context, R.color.on_primary))
                } else {
                    setTextColor(getColor(context, paramTextColor))
                }
                layoutParams = params
                text = content
                textSize = if (paramTextSize === null) {
                    15F
                } else {
                    paramTextSize
                }
            }
        }

        fun loadLeaderboard(resultLayout: LinearLayout,selfResultLayout:RelativeLayout, quizId: Int, token: String) = runBlocking {
            lateinit var results: JSONArray
            lateinit var selfResults:JSONObject
            val context = resultLayout.context as LeaderboardActivity
            launch {
                results = JSONArray(SQLConnector.serverCall("GET", "leaderboard/$quizId")[1])
                selfResults = JSONObject(SQLConnector.serverCall("GET","ranking/$quizId", token = token)[1]).getJSONObject("user")
            }.join()
            for (i in 0 until results.length()) {
                val result = results.getJSONObject(i)
                val rank = result.getInt("rank")
                val points = result.getInt("points")
                val userName = result.getString("name")

                val rowLayout = RelativeLayout(context)
                resultLayout.addView(rowLayout)
                with(rowLayout) {
                    layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                    layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                    background = AppCompatResources.getDrawable(context, R.drawable.answer)
                    backgroundTintList = context.getColorStateList(R.color.primary)
                    setPadding(
                        0,
                        toPX(context, 5),
                        0,
                        toPX(context, 5)
                    )
                }

                addResultTextView(
                    context,rowLayout, "#$rank", RelativeLayout.CENTER_VERTICAL
                )

                addResultTextView(
                    context,rowLayout, userName, RelativeLayout.CENTER_IN_PARENT
                )

                addResultTextView(
                    context,
                    rowLayout,
                    points.toString(),
                    RelativeLayout.CENTER_VERTICAL,
                    RelativeLayout.ALIGN_PARENT_RIGHT
                )

                selfResultLayout.removeAllViews()
                addResultTextView(
                    context,
                    selfResultLayout,
                    "#${selfResults.getString("rank")}",
                    RelativeLayout.CENTER_VERTICAL,
                    paramTextSize = 18F,
                    paramTextColor = R.color.primary_variant
                    )
                addResultTextView(
                    context,
                    selfResultLayout,
                    selfResults.getString("name"),
                    RelativeLayout.CENTER_IN_PARENT,
                    paramTextSize = 18F,
                    paramTextColor = R.color.primary_variant
                    )
                addResultTextView(
                    context,
                    selfResultLayout,
                    selfResults.getString("points"),
                    RelativeLayout.CENTER_VERTICAL,
                    RelativeLayout.ALIGN_PARENT_RIGHT,
                    paramTextSize = 18F,
                    paramTextColor = R.color.primary_variant
                    )
            }
        }
    }
}