package hu.petrik.quizion.activities

import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.R
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.components.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Suppress("MemberVisibilityCanBePrivate", "unused")
class Game(quiz: Quiz, token: String, numberOfQuestions: Int, delay: Int = 0) {
    var quiz: Quiz
        private set
    var token: String
        private set
    var answers = ArrayList<Answer>()
    var correctAnswers = ArrayList<Int>()
    var points: Int = 0
        private set
    var numberOfQuestions: Int
        private set
    var currentIndex: Int = 0
        private set
    var delay: Int = 0
        private set

    companion object {
        @Suppress("SpellCheckingInspection")
        fun newGame(quiz_id: Int, token: String): Game = runBlocking {
            lateinit var quiz: Quiz
            var count = 0
            val now = LocalDateTime.now()
            launch {
                quiz = Quiz.getById(quiz_id)
                count = JSONObject(
                    SQLConnector.serverCall(
                        "GET",
                        "quiz/$quiz_id/questions/count"
                    )[1]
                ).getInt("count")
                val result = SQLConnector.serverCall(
                    "POST",
                    "play/newgame/$quiz_id",
                    token = token
                )
                Log.d("Új játék / ${result[0]}", result[1])
            }.join()
            val delay = Duration.between(now, LocalDateTime.now()).toMillis().toInt() + 100
            return@runBlocking Game(quiz, token, count, delay)
        }
    }

    init {
        this.quiz = quiz
        this.token = token
        this.numberOfQuestions = numberOfQuestions
        this.delay = delay
    }

    fun play(binding: ActivityMainBinding) {
        (binding.root.context as MainActivity).setCompletionLimit(this.numberOfQuestions)
        Log.d("play: Number of questions", this.numberOfQuestions.toString())
        this.loadCurrent(binding)
    }

    suspend fun getCurrentQuestion(): Question {
        val result = JSONObject(
            SQLConnector.serverCall(
                "GET", "play/${this.quiz.id}/question", token = token
            )[1]
        )
        if (result.has("result")) {
            throw Exception(result.toString())
        } else {
            return Question(result)
        }
    }

    suspend fun getCurrentAnswers(): ArrayList<Answer> {
        val response = JSONArray(
            SQLConnector.serverCall(
                "GET", "play/${this.quiz.id}/answers", token = token
            )[1]
        )
        val list = java.util.ArrayList<Answer>()
        for (i in 0 until response.length()) {
            val item = response.getJSONObject(i)
            list.add(Answer(item))
        }
        return list
    }

    fun sendResults(binding: ActivityMainBinding) = runBlocking {
        val params = JSONObject()
        val chosen = JSONArray()
        val context = binding.root.context as MainActivity
        context.stopTimer()
        for (answer in answers) {
            if (answer.isChosen) {
                chosen.put(answer.id)
            }
        }
        logAnswers()
        params.put("chosen", chosen)
        Log.d("Chosen", chosen.toString())
        lateinit var response: ArrayList<String>
        launch {
            response = SQLConnector.serverCall(
                "POST", "play/${this@Game.quiz.id}/choose", params, this@Game.token
            )
        }.join()
        when {
            response[0].toInt() == 408 -> {
                Toast.makeText(context, "Timed out!", Toast.LENGTH_SHORT).show()
                context.toogleNextButton()
            }
            response[0].toInt() == 404 -> {
                ViewSwapper.swapActivity(
                    binding.root.context,
                    QuizzesActivity(),
                    Pair("quiz_id", this@Game.quiz.id.toString()),
                    Pair("result", "timeout"),
                    Pair("token", this@Game.token)
                )
            }
            else -> {
                loadSuccess(binding, JSONArray(response[1]))
            }
        }
    }


    fun loadCurrent(binding: ActivityMainBinding) = runBlocking {
        lateinit var question: Question
        lateinit var answers: ArrayList<Answer>
        var current = 0
        var ended = false
        launch {
            try {
                question = getCurrentQuestion()
                answers = getCurrentAnswers()
                Log.d(binding.root.context.getString(R.string.answers), answers.toString())
                val id = this@Game.quiz.id
                current = JSONObject(
                    SQLConnector.serverCall(
                        "GET",
                        "play/${id.toString()}/state",
                        null,
                        token
                    )[1]
                ).getInt("current")
            } catch (e: Exception) {
                val result = JSONObject(e.message!!)
                if (result.has("result")) {
                    ViewSwapper.swapActivity(
                        binding.root.context,
                        QuizzesActivity(),
                        Pair("quiz_id", this@Game.quiz.id.toString()),
                        Pair("result", result.getString("result")),
                        Pair("Token", token)
                    )
                    ended = true
                    return@launch
                }
            }
        }.join()
        if (!ended) {
            val context = binding.root.context as MainActivity
            context.setCompletionState(current)
            try {
                if (answers.isNotEmpty()) {
                    this@Game.answers.clear()
                    ViewBuilder.loadQuestion(binding.textViewKerdes!!, question.content)
                    val ids = ViewBuilder.loadAnswerAll(
                        binding.root.context as MainActivity,
                        binding.layoutValaszok,
                        answers
                    )
                    for (i in 0 until answers.size) {
                        val activity = binding.root.context as MainActivity
                        val id = ids[i]
                        this@Game.answers.add(answers[i])
                        answers[i].buttonID = id
                        (activity.findViewById(id) as MaterialButton).setOnClickListener {
                            activity.game.answerEvent(activity, id)
                        }
                    }
                    context.initializeTimer(this@Game.quiz.secondsPerQuiz * 1000 - this@Game.delay)
                    context.showTimerBar()
                } else {
                    ViewBuilder.loadQuestion(
                        binding.textViewKerdes!!,
                        binding.root.context.getString(R.string.connection_failed)
                    )
                }
            } catch (e: Exception) {
                ViewBuilder.loadQuestion(binding.textViewKerdes!!, e.message)
            }
        }
    }

    fun answerEvent(context: MainActivity, id: Int) {
        val answer = this.answers.find { it.buttonID == id }!!
        answer.switchChosen()
        context.setAnswerState(
            answer.buttonID!!,
            if (answer.isChosen) AnswerState.SELECTED else AnswerState.DEFAULT
        )
        if (answer.isChosen && this.getSelectedCount() == 1) {
            context.showNextButton()
        } else if (!answer.isChosen && this.getSelectedCount() == 0) {
            context.hideNextButton()
        }
    }

    fun getSelectedCount(): Int {
        var count = 0
        for (answer in this.answers) {
            if (answer.isChosen) {
                count++
            }
        }
        return count
    }

    fun loadSuccess(binding: ActivityMainBinding, response: JSONArray) {
        val context = binding.root.context as MainActivity
        Log.d("loadSuccess: JSON", response.toString())
        for (i in 0 until response.length()) {
            val element = response.getJSONObject(i)
            for (answer in this.answers) {
                val id = element.getInt("id")
                if (id == answer.id) {
                    val result = element.getInt("is_right")
                    when {
                        result == 1 && answer.isChosen -> {
                            context.setAnswerState(answer.buttonID!!, AnswerState.CHOSEN_CORRECT)
                        }
                        result == 1 && !answer.isChosen -> {
                            context.setAnswerState(answer.buttonID!!, AnswerState.MISSING_CORRECT)
                        }
                        result == 0 && answer.isChosen -> {
                            context.setAnswerState(answer.buttonID!!, AnswerState.CHOSEN_INCORRECT)
                        }
                    }
                }
                context.findViewById<MaterialButton>(answer.buttonID!!).setOnClickListener {
                    context.game.loadCurrent(binding)
                }
            }
        }
    }

    fun logAnswers() {
        Log.d("answerEvent / answers.size", this.answers.size.toString())
        for (i in 0 until this.answers.size) {
            Log.d("answerEvent / Answer", answers[i].toString())
        }
    }
}