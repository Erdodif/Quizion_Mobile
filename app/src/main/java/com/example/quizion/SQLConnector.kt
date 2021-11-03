package com.example.quizion

import com.example.quizion.elemek.Question
import com.example.quizion.elemek.Answer
import com.example.quizion.elemek.Quiz

import java.net.HttpURLConnection
import org.json.JSONObject
import java.lang.Exception
import org.json.JSONArray
import android.util.Log
import java.net.URL
import java.util.*

class SQLConnector {
    companion object {
        private var threadLocked = false

        fun APIhivas(params: String): String? {
            if (threadLocked) {
                Log.d("Figyelmeztetés", "Szál foglalva!")
                return null
            }
            threadLocked = true;
            Log.d("Mellékszál állapota", "fut")
            val baseUrl = "http://10.147.20.1/adatok/index.php?"
            val connection = URL(baseUrl + params).openConnection() as HttpURLConnection
            var kiad: String? = ""
            try {
                val data = connection.inputStream.bufferedReader().readText()
                Log.d("Kérés állapota:", "Adat visszatért!")
                kiad = data
            } catch (e: Exception) {
                Log.d("Hiba", e.message.toString())
                kiad = null
            } finally {
                connection.disconnect()
                threadLocked = false;
            }
            return kiad
        }

        fun getQuizAll(): List<Quiz> {
            val quizJSON = APIhivas("method=read&table=quiz")
            val jsonContact: JSONObject = JSONObject(quizJSON!!)
            val jsonArray: JSONArray = jsonContact.getJSONArray("data")
            var i = 0;
            val size = jsonArray.length()
            var quizList = LinkedList<Quiz>()
            for (i in 0 until size) {
                val elem = jsonArray.getJSONObject(i)
                val quiz: Quiz = Quiz(
                    elem.getInt("id"),
                    elem.getString("header"),
                    elem.getString("description"),
                    elem.getInt("active")
                )
                quizList.add(quiz)
            }
            return quizList
        }

        fun getQuestionAll() :List<Question>{
            val quizJSON = APIhivas("method=read&table=question")
            val jsonContact: JSONObject = JSONObject(quizJSON!!)
            val jsonArray: JSONArray = jsonContact.getJSONArray("data")
            var i = 0;
            val size = jsonArray.length()
            var questionList = LinkedList<Question>()
            for (i in 0 until size) {
                val elem = jsonArray.getJSONObject(i)
                val question: Question = Question(
                    elem.getInt("id"),
                    elem.getInt("quiz_id"),
                    elem.getString("content"),
                    elem.getInt("no_right_answers"),
                    elem.getInt("point")
                )
                questionList.add(question)
            }
            return questionList
        }

        fun getAnswerAll() :List<Answer>{
            val quizJSON = APIhivas("table=answer")
            val jsonContact: JSONObject = JSONObject(quizJSON!!)
            val jsonArray: JSONArray = jsonContact.getJSONArray("data")
            var i = 0;
            val size = jsonArray.length()
            var answerList = LinkedList<Answer>()
            for (i in 0 until size) {
                val elem = jsonArray.getJSONObject(i)
                val answer: Answer = Answer(
                    elem.getInt("id"),
                    elem.getInt("question_id"),
                    elem.getString("content"),
                    elem.getInt("is_right")
                )
                answerList.add(answer)
            }
            return answerList
        }

        fun init() {
            var objectList = LinkedList<Any>()
            objectList.addAll(getQuizAll())
            objectList.addAll(getQuestionAll())
            objectList.addAll(getAnswerAll())
            for (i in objectList){
                Log.d("Elem",i.toString())
            }
        }


    }
}