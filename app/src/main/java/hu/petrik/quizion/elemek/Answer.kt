package hu.petrik.quizion.elemek

import hu.petrik.quizion.adatbazis.SQLConnector.Companion.apiHivas
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Answer {
    var id: Int?
        private set
    var content: String
        private set

    constructor(
        id: Int?,
        content: String,
    ) {
        this.id = id
        this.content = content
    }

    constructor(jsonObject: JSONObject) {
        this.id = jsonObject.get("id") as Int?
        this.content = jsonObject.getString("content")
    }

    companion object {
        suspend fun getAll(): ArrayList<Answer> {
            val response = JSONArray(apiHivas("GET", "answers"))
            val list = ArrayList<Answer>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Answer(item))
            }
            return list
        }

        suspend fun getAllByQuiz(id: Int, order: Int): ArrayList<Answer> {
            val response =
                JSONArray(apiHivas("GET", "quiz/$id/question/$order/answers"))
            val list = ArrayList<Answer>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Answer(item))
            }
            return list
        }

        suspend fun getByQuiz(id: Int, questionOrder: Int, answerOrder: Int): Answer {
            return Answer(
                JSONObject(
                    apiHivas(
                        "GET",
                        "quiz/$id/question/$questionOrder/answer/$answerOrder"
                    )[1]
                )
            )
        }

        suspend fun getAllByQuestion(questionId: Int): ArrayList<Answer> {
            val response =
                JSONArray(apiHivas("GET", "question/$questionId/answers"))
            val list = ArrayList<Answer>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Answer(item))
            }
            return list
        }

        suspend fun getByQuestion(questionId: Int, answerOrder: Int): Answer {
            return Answer(
                JSONObject(
                    apiHivas(
                        "GET",
                        "/question/$questionId/answer/$answerOrder"
                    )[1]
                )
            )
        }

        suspend fun getById(id: Int): Answer {
            return Answer(JSONObject(apiHivas("GET", "answer/$id")[1]))
        }
    }
}