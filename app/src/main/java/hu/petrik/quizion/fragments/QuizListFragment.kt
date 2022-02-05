package hu.petrik.quizion.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.MainActivity
import hu.petrik.quizion.activities.QuizzesActivity
import hu.petrik.quizion.components.Quiz
import hu.petrik.quizion.components.ViewBuilder
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
            val quizzes = Quiz.getAllActive()
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

    fun loadQuizAll(quitLayout: LinearLayout, content: List<Quiz>, token: String) {
        for (elem in content) {
            val lp = LinearLayout.LayoutParams(quitLayout.layoutParams)
            lp.setMargins(20, 10, 20, 10)
            val layout = LinearLayout(context)
            layout.apply {
                setPadding(ViewBuilder.toPX(activity as Activity, 15))
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
            val playButton = MaterialButton(context as Context)
            lp.setMargins(20, 0, 20, 0)
            playButton.apply {
                isAllCaps = false
                text = resources.getText(R.string.play)
                layoutParams = lp
                setPadding(ViewBuilder.toPX(activity as Activity, 15))
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
                intent.putExtra("Token", token)
                (context as Context).startActivity(intent)
                requireActivity().finish()
            }
            layout.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            quitLayout.addView(layout)
            Log.d(requireActivity().getString(R.string.header), elem.header)
            Log.d(requireActivity().getString(R.string.description), elem.description)
        }
        quitLayout.measure(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun loadLabelError(quizLayout: LinearLayout, error: String) {
        requireActivity().runOnUiThread {
            quizLayout.removeAllViews()
            val layout = LinearLayout(context)
            val lp = LinearLayout.LayoutParams(quizLayout.layoutParams)
            lp.setMargins(20, 10, 20, 10)
            layout.apply {
                setPadding(ViewBuilder.toPX(activity as Activity, 15))
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

}