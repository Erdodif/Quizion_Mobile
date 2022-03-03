package hu.petrik.quizion.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.QuizzesActivity
import hu.petrik.quizion.controllers.ViewBuilder
import hu.petrik.quizion.controllers.ViewSwapper
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.FragmentLeaderboardBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardFragment : Fragment() {
    private lateinit var bind: FragmentLeaderboardBinding
    private var loginInProgress = false

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentLeaderboardBinding.inflate(inflater, container, false)
        init()
        return bind.root
    }

    private fun init() {
        val arguments = requireArguments()
        val result = arguments.getString("result")
        val quizId = arguments.getString("quiz_id")!!.toInt()
        val token = (activity as QuizzesActivity).token
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
        loadLeaderboard(bind.layoutLeaderboard, bind.layoutResultSelf, quizId, token)
        bind.buttonBack.setOnClickListener {
            ViewSwapper.swapActivity(
                activity as Activity,
                QuizzesActivity(),
                Pair("Token", token),
                finish = true
            )
        }
    }

    private fun loadLeaderboard(
        resultLayout: LinearLayout,
        selfResultLayout: RelativeLayout,
        quizId: Int,
        token: String
    ) = runBlocking {
        lateinit var results: JSONArray
        lateinit var selfResults: JSONObject
        val context = resultLayout.context as QuizzesActivity
        launch {
            results = JSONArray(SQLConnector.serverCall("GET", "leaderboard/$quizId")[1])
            selfResults = JSONObject(
                SQLConnector.serverCall(
                    "GET",
                    "ranking/$quizId",
                    token = token
                )[1]
            ).getJSONObject("users")
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
                backgroundTintList = context.getColorStateList(R.color.colorPrimary)
                setPadding(
                    0,
                    ViewBuilder.toPX(context, 5),
                    0,
                    ViewBuilder.toPX(context, 5)
                )
            }

            addResultTextView(
                context, rowLayout, "#$rank", RelativeLayout.CENTER_VERTICAL
            )

            addResultTextView(
                context, rowLayout, userName, RelativeLayout.CENTER_IN_PARENT
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
                paramTextColor = R.color.colorPrimaryDark
            )
            addResultTextView(
                context,
                selfResultLayout,
                selfResults.getString("name"),
                RelativeLayout.CENTER_IN_PARENT,
                paramTextSize = 18F,
                paramTextColor = R.color.colorPrimaryDark
            )
            addResultTextView(
                context,
                selfResultLayout,
                selfResults.getString("points"),
                RelativeLayout.CENTER_VERTICAL,
                RelativeLayout.ALIGN_PARENT_RIGHT,
                paramTextSize = 18F,
                paramTextColor = R.color.colorPrimaryDark
            )
        }
    }

    private fun addResultTextView(
        context: QuizzesActivity,
        to: RelativeLayout,
        content: String,
        vararg rules: Int,
        paramTextSize: Float? = null,
        @ColorRes paramTextColor: Int? = null
    ) {
        val textView = TextView(context)
        to.addView(textView)
        val params = textView.layoutParams as RelativeLayout.LayoutParams
        with(params) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            for (i in rules.indices) {
                addRule(rules[i], 1)
            }
            marginStart = ViewBuilder.toPX(context, 15)
            marginEnd = ViewBuilder.toPX(context, 15)
        }
        with(textView) {
            if (paramTextColor === null) {
                setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
            } else {
                setTextColor(ContextCompat.getColor(context, paramTextColor))
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
}