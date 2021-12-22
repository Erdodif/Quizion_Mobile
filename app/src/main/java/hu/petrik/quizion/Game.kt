package hu.petrik.quizion

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.UiThread
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Quiz
import hu.petrik.quizion.elemek.ViewBuilder
import hu.petrik.quizion.elemek.AnswerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

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
        fun newGame(quiz_id: Int, token: String): Game = runBlocking {
            lateinit var quiz: Quiz
            var count = 0
            launch {
                quiz = Quiz.getById(quiz_id)
                count = JSONObject(
                    SQLConnector.apiHivas(
                        "GET",
                        "quiz/$quiz_id/questions/count"
                    )[1]
                ).getInt("count")
                val result = SQLConnector.apiHivas(
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
                SQLConnector.apiHivas(
                    "GET", "play/${this.quiz.id}/question", token = token
                )[1]
            )
        )
    }

    suspend fun getCurrentAnswers(): ArrayList<Answer> {
        val response = JSONArray(
            SQLConnector.apiHivas(
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

    fun loadCurrent(binding: ActivityMainBinding) = runBlocking {
        lateinit var kerdes: Question
        lateinit var valaszok: ArrayList<Answer>
        launch {
            kerdes = getCurrentQuestion()
            valaszok = getCurrentAnswers()
            Log.d("valaszok", valaszok.toString())
        }.join()

        try {
            if (valaszok.isNotEmpty()) {
                ViewBuilder.kerdesBetolt(binding.textViewKerdes!!, kerdes.content)
                val ids = ViewBuilder.valaszBetoltMind(
                    binding.root.context as Activity,
                    binding.layoutValaszok,
                    valaszok
                )
                for (i in 0 until valaszok.size){
                    val activity = binding.root.context as MainActivity
                    val id = ids[i]
                    answers.add(valaszok[i])
                    valaszok[i].buttonID = id
                    (activity.findViewById(id) as MaterialButton).setOnClickListener {
                        activity.game.answerEvent(activity,id)
                    }
                }
            } else {
                ViewBuilder.kerdesBetolt(
                    binding.textViewKerdes!!,
                    "Nem Sikerült csatlakozni"
                )
            }
        } catch (e: Exception) {
            ViewBuilder.kerdesBetolt(binding.textViewKerdes!!, e.message)
        }
    }

    fun answerEvent(context: MainActivity, id: Int) {
        val answer = this.answers.find { it.buttonID == id }!!
        answer.switchChoosen()
        context.setAnswerState(
            answer.buttonID!!,
            if (answer.isChoosen) AnswerState.SELECTED else AnswerState.DEFAULT
        )
        if (answer.isChoosen && this.getSelectedCount() == 1){
            context.showNextButton()
        }
        else if (!answer.isChoosen && this.getSelectedCount() == 0){
            context.hideNextButton()
        }
    }

    fun getSelectedCount():Int{
        var count = 0
        for (answer in this.answers) {
            if (answer.isChoosen){
                count++
            }
        }
        return count
    }

    fun logAnswers(){
        Log.d("answerEvent / answers.size", this.answers.size.toString())
        for(i in 0 until this.answers.size){
            Log.d("answerEvent / Answer", answers[i].toString())
        }
    }
}