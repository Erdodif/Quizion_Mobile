package hu.petrik.quizion.elemek

import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector.Companion.apiHivas
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Question {
    var id: Int?
        private set
    var content: String
        private set
    var noRightAnswers: Int
        private set
    var point: Int
        private set

    constructor(
        id: Int?,
        quiz_id: Int,
        noRightAnswers: Int,
        content: String,
        point: Int
    ) {
        this.id = id
        this.noRightAnswers = noRightAnswers
        this.content = content
        this.point = point
    }

    constructor(jsonObject: JSONObject) {
        this.id = jsonObject.get("id") as Int?
        this.content = jsonObject.getString("content")
        this.noRightAnswers = jsonObject.getInt("no_right_answers")
        this.point = jsonObject.getInt("point")
    }


    companion object {
        suspend fun getAll(): ArrayList<Question> {
            val response = JSONArray(apiHivas(Method.READ, "questions"))
            val list = ArrayList<Question>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Question(item))
            }
            return list
        }

        suspend fun getById(id: Int): Question {
            return Question(JSONObject(apiHivas(Method.READ, "question/$id")!!))
        }

        suspend fun getAllByQuiz(id: Int): ArrayList<Question> {
            val response = JSONArray(apiHivas(Method.READ, "quiz/$id/questions"))
            val list = ArrayList<Question>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Question(item))
            }
            return list
        }

        suspend fun getAllByQuiz(quiz: Quiz): ArrayList<Question> {
            return getAllByQuiz(quiz.id!!)
        }

        suspend fun getByQuiz(id: Int, order: Int): Question {
            return Question(JSONObject(apiHivas(Method.READ, "quiz/$id/question/$order")!!))
        }

        suspend fun getByQuiz(quiz: Quiz, order: Int): Question {
            return getByQuiz(quiz.id!!, order)
        }
    }
}