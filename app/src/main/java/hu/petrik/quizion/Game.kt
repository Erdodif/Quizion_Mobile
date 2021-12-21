package hu.petrik.quizion

import android.app.Activity
import android.util.Log
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Quiz
import hu.petrik.quizion.elemek.ViewBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

class Game(quiz: Quiz, token: String, numberOfQuestions: Int) {
    var quiz: Quiz
        private set
    var token: String
        private set
    var points: Int = 0
        private set
    var numberOfQuestions: Int
        private set
    var currentIndex: Int = 0
        private set

    companion object {
        fun newGame(quiz_id: Int, token: String): Game = runBlocking {
            lateinit var quiz: Quiz
            var count: Int= 0
            lateinit var questions: ArrayList<Question>
            val io = launch {
                quiz = Quiz.getById(quiz_id)
                count = JSONObject(
                SQLConnector.apiHivas(
                    "GET",
                    "quiz/$quiz_id/questions/count"
                )[1]
            ).getInt("count")
            }.join()
            SQLConnector.apiHivas("POST", "play/newgame/$quiz_id", token = token)
            return@runBlocking Game(quiz,token,count)
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

    suspend fun getCurrentQuestion():Question{
        return Question(JSONObject(SQLConnector.apiHivas("GET","play/${this.quiz.id}/question",token = token)[1]))
    }

    suspend fun getCurrentAnswers(): ArrayList<Answer> {
        val response = JSONArray(SQLConnector.apiHivas("GET", "play/${this.quiz.id}/answers",token = token)[1])
        val list = java.util.ArrayList<Answer>()
        for (i in 0 until response.length()) {
            val item = response.getJSONObject(i)
            list.add(Answer(item))
        }
        return list
    }

    fun loadCurrent(binding: ActivityMainBinding) = runBlocking {
        lateinit var kerdes:Question
        lateinit var valaszok:ArrayList<Answer>
        launch {
            kerdes = getCurrentQuestion()
            valaszok = getCurrentAnswers()
            Log.d("valaszok", valaszok.toString())
        }.join()
        try {
            if (valaszok.isNotEmpty()) {
                ViewBuilder.kerdesBetolt(binding.textViewKerdes!!, kerdes.content)
                ViewBuilder.valaszBetoltMind(
                    binding.root.context as Activity,
                    binding.layoutValaszok,
                    valaszok
                )
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

}