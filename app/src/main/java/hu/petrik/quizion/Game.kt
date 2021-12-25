package hu.petrik.quizion

import android.app.Activity
import android.util.Log
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Quiz
import hu.petrik.quizion.elemek.ViewBuilder
import hu.petrik.quizion.elemek.AnswerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

@Suppress("MemberVisibilityCanBePrivate", "unused")
class Game(quiz: Quiz, token: String, numberOfQuestions: Int) {
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

    companion object {
        @Suppress("SpellCheckingInspection")
        fun newGame(quiz_id: Int, token: String): Game = runBlocking {
            lateinit var quiz: Quiz
            var count = 0
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
            return@runBlocking Game(quiz, token, count)
        }
    }

    init {
        this.quiz = quiz
        this.token = token
        this.numberOfQuestions = numberOfQuestions
    }

    fun play(binding: ActivityMainBinding) {
        this.loadCurrent(binding)
    }

    suspend fun getCurrentQuestion(): Question {
        return Question(
            JSONObject(
                SQLConnector.serverCall(
                    "GET", "play/${this.quiz.id}/question", token = token
                )[1]
            )
        )
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
        for (answer in answers) {
            if (answer.isChosen) {
                chosen.put(answer.id)
            }
        }
        params.put("chosen", chosen)
        lateinit var response: ArrayList<String>
        launch {
            response = SQLConnector.serverCall(
                "POST", "play/${this@Game.quiz.id}/choose", params, this@Game.token
            )
        }.join()
        loadSuccess(binding, JSONObject(response[1]))
    }


    fun loadCurrent(binding: ActivityMainBinding) = runBlocking {
        lateinit var question: Question
        lateinit var answers: ArrayList<Answer>
        var current: Int = 0
        launch {
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
        }.join()
        (binding.root.context as MainActivity).setCompletionState(current)
        try {
            if (answers.isNotEmpty()) {
                ViewBuilder.loadQuestion(binding.textViewKerdes!!, question.content)
                val ids = ViewBuilder.loadAnswerAll(
                    binding.root.context as Activity,
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

    fun loadSuccess(binding: ActivityMainBinding, response: JSONObject) {
        val context = binding.root.context as MainActivity
        for (answer in this.answers) {
            val current = response.getInt(answer.id.toString())
            when {
                current == 1 && answer.isChosen -> {
                    context.setAnswerState(answer.buttonID!!, AnswerState.CHOSEN_CORRECT)
                }
                current == 1 && !answer.isChosen -> {
                    context.setAnswerState(answer.buttonID!!, AnswerState.MISSING_CORRECT)
                }
                current == 0 && answer.isChosen -> {
                    context.setAnswerState(answer.buttonID!!, AnswerState.CHOSEN_INCORRECT)
                }
            }
            context.findViewById<MaterialButton>(answer.buttonID!!).setOnClickListener {
                context.game.loadCurrent(binding)
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