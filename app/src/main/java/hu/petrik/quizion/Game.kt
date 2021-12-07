package hu.petrik.quizion

import android.app.Activity
import android.util.Log
import android.widget.Toast
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Quiz
import hu.petrik.quizion.elemek.ViewBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Game(quiz:Quiz,questions: ArrayList<Question>) {
    lateinit var quiz: Quiz
        private set
    lateinit var questions: ArrayList<Question>
        private set
    var points: Int = 0
        private set
    var actual: Int = 0
        private set

    companion object{
        fun newGame(quiz_id: Int):Game = runBlocking{
            lateinit var quiz :Quiz
            lateinit var questions:ArrayList<Question>
            val io = launch {
                quiz = Quiz.getById(quiz_id)
                questions = Question.getAllByQuiz(quiz)
            }.join()
            return@runBlocking Game(quiz,questions)
        }
    }
    init {
        this.quiz = quiz
        this.questions = questions
    }

    fun toNext(){
        this.actual++
    }

    fun hasNext(): Boolean {
        return actual + 1 != questions.size
    }

    fun play(binding: ActivityMainBinding, order:Int = 0) {
        this.loadQuiz(binding, order)
    }

    fun loadQuiz(binding: ActivityMainBinding, index: Int = 0) = runBlocking {
        val anwerTolt = launch {
            val valaszok = Answer.getAllByQuestion(questions[index].id!!)
            Log.d("valaszok", valaszok.toString())
            try {
                if (valaszok.isNotEmpty()) {
                    ViewBuilder.kerdesBetolt(binding.textViewKerdes!!, questions[index].content)
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
        anwerTolt.join()
    }
}