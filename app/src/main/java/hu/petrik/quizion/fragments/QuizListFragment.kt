package hu.petrik.quizion.fragments

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginStart
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.MainActivity
import hu.petrik.quizion.activities.QuizzesActivity
import hu.petrik.quizion.components.Quiz
import hu.petrik.quizion.controllers.ViewBuilder
import hu.petrik.quizion.controllers.ViewSwapper
import hu.petrik.quizion.databinding.FragmentQuizListBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QuizListFragment : Fragment() {

    private lateinit var bind: FragmentQuizListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentQuizListBinding.inflate(inflater, container, false)
        init()
        return bind.root
    }

    private fun init() {
        val token = (activity as QuizzesActivity).token
        loadQuizzes(bind.layoutQuizList, token)
        bind.layoutQuizList.removeView(bind.tempLayout)
    }

    private fun loadQuizzes(quizLayout: LinearLayout, token: String): Unit = runBlocking {
        launch {
            Log.d("Coroutine state", "running")
            val params: HashMap<String, Any> = HashMap()
            params["active"] = 1
            val quizzes = Quiz.getAllActive(token)
            try {
                if (quizzes.isNotEmpty()) {
                    loadQuizAll(quizLayout, quizzes, token)
                } else {
                    loadLabelError(
                        quizLayout,
                        getString(R.string.server_connection_error)
                    )
                }
            } catch (e: Exception) {
                loadLabelError(quizLayout, e.message.toString())
            }
        }.join()
    }

    private fun loadQuizAll(quizLayout: LinearLayout, content: List<Quiz>, token: String) {
        for (elem in content) {
            val lp = LinearLayout.LayoutParams(quizLayout.layoutParams)
            lp.setMargins(20, 20, 20, 20)
            val layout = LinearLayout(context)
            layout.apply {
                setPadding(ViewBuilder.toPX(activity as Activity, 15))
                layoutParams = lp
                background = AppCompatResources.getDrawable(context, R.color.textColorPrimary)
                orientation = LinearLayout.VERTICAL
            }
            val header = TextView(context)
            lp.setMargins(10)
            header.apply {
                layoutParams = lp
                text = elem.header
                isAllCaps = false
                textSize = 20F
                setTextColor(context.getColor(R.color.colorPrimaryDark))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            val description = TextView(context)
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
            description.apply {
                layoutParams = lp
                text = elem.description
                isAllCaps = false
                textSize = 16F
                setTextColor(context.getColor(R.color.colorPrimaryDark))
            }
            layout.addView(header)
            layout.addView(description)
            val buttonHolder = createButtonHolder(elem.id!!, token)
            layout.addView(buttonHolder)
            layout.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            quizLayout.addView(layout)
            Log.d(requireActivity().getString(R.string.header), elem.header)
            Log.d(requireActivity().getString(R.string.description), elem.description)
        }
        quizLayout.measure(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun createButtonHolder(id: Int, token: String): LinearLayout {
        val activity = activity as Activity
        val playLp = LinearLayout.LayoutParams(
            0,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            1F
            //RelativeLayout.LayoutParams.WRAP_CONTENT,
        )
        val leaderLp = LinearLayout.LayoutParams(
            0,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            1F
            //RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val playButton = MaterialButton(
            context as Activity,
            null,
            R.style.action_button
        )
        val leaderboardButton = MaterialButton(
            context as Activity,
            null,
            R.style.action_button
        )
        playButton.id = LinearLayout.generateViewId()
        leaderboardButton.id = LinearLayout.generateViewId()
        with(playLp) {
            marginStart = ViewBuilder.toPX(activity, 20)
            marginEnd= ViewBuilder.toPX(activity, 10)
        }
        with(leaderLp) {
            marginEnd= ViewBuilder.toPX(activity, 20)
            marginStart = ViewBuilder.toPX(activity, 10)
        }
        playButton.apply {
            text = resources.getText(R.string.play)
            layoutParams = playLp
            textSize = 20F
            backgroundTintList = context.getColorStateList(R.color.textColorSecondary)
            textAlignment = MaterialButton.TEXT_ALIGNMENT_CENTER
            setTextColor(context.getColor(R.color.textColorPrimary))
            cornerRadius = ViewBuilder.toPX(context as Activity, 10)
            setPadding(
                ViewBuilder.toPX(activity, 10)
            )
        }
        leaderboardButton.apply {
            text = resources.getText(R.string.leaderboard)
            layoutParams = leaderLp
            textSize = 20F
            backgroundTintList = context.getColorStateList(R.color.textColorSecondary)
            textAlignment = MaterialButton.TEXT_ALIGNMENT_CENTER
            setTextColor(context.getColor(R.color.textColorPrimary))
            cornerRadius = ViewBuilder.toPX(context as Activity, 10)
            setPadding(
                ViewBuilder.toPX(activity, 10)
            )
        }
        val buttonHolder = LinearLayout(context as Activity)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonHolder.layoutParams = lp
        lp.topMargin = ViewBuilder.toPX(activity,10)
        buttonHolder.orientation = LinearLayout.HORIZONTAL
        buttonHolder.addView(leaderboardButton)
        buttonHolder.addView(playButton)
        playButton.setOnClickListener {
            ViewSwapper.swapActivity(
                context as Context,
                MainActivity(),
                Pair("id", id.toString()),
                Pair("Token", token),
                finish = true
            )
        }
        leaderboardButton.setOnClickListener {
            (activity as QuizzesActivity).showLeaderBoard("", id.toString())
        }
        return buttonHolder
    }

    private fun loadLabelError(quizLayout: LinearLayout, error: String) {
        requireActivity().runOnUiThread {
            quizLayout.removeAllViews()
            val layout = LinearLayout(context)
            val lp = LinearLayout.LayoutParams(quizLayout.layoutParams)
            lp.setMargins(20, 10, 20, 10)
            layout.apply {
                setPadding(ViewBuilder.toPX(activity as Activity, 15))
                layoutParams = lp
                background = AppCompatResources.getDrawable(context, R.color.textColorPrimary)
                orientation = LinearLayout.VERTICAL
            }
            val header = TextView(context)
            lp.setMargins(20, 0, 20, 0)
            header.apply {
                layoutParams = lp
                text = context.getString(R.string.error)
                isAllCaps = false
                textSize = 20F
                setTextColor(context.getColor(R.color.colorPrimaryDark))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            val description = TextView(context)
            lp.setMargins(20, 10, 20, 10)
            description.apply {
                layoutParams = lp
                text = error
                textSize = 20F
                setTextColor(context.getColor(R.color.colorPrimaryDark))
                textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            }
            quizLayout.addView(layout, lp)
            layout.addView(header)
            layout.addView(description)
        }
    }

}