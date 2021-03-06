package hu.petrik.quizion.components

import hu.petrik.quizion.database.SQLConnector.Companion.serverCall
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Suppress("unused")
class Answer {
    var id: Int?
        private set
    var content: String
        private set
    var buttonID: Int? = null
    var isChosen: Boolean = false
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

    fun switchChosen() {
        this.isChosen = !this.isChosen
    }

    companion object {
        suspend fun getAll(): ArrayList<Answer> {
            val response = JSONArray(serverCall("GET", "answers"))
            val list = ArrayList<Answer>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Answer(item))
            }
            return list
        }

        suspend fun getAllByQuiz(id: Int, order: Int): ArrayList<Answer> {
            val response =
                JSONArray(serverCall("GET", "quizzes/$id/questions/$order/answers"))
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
                    serverCall(
                        "GET",
                        "quizzes/$id/questions/$questionOrder/answers/$answerOrder"
                    )[1]
                )
            )
        }

        suspend fun getAllByQuestion(questionId: Int): ArrayList<Answer> {
            val response =
                JSONArray(serverCall("GET", "questions/$questionId/answers"))
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
                    serverCall(
                        "GET",
                        "/questions/$questionId/answers/$answerOrder"
                    )[1]
                )
            )
        }

        suspend fun getById(id: Int): Answer {
            return Answer(JSONObject(serverCall("GET", "answers/$id")[1]))
        }
    }

    override fun toString(): String {
        return "Answer | Id: $id, Button Id: $buttonID, ${
            if (isChosen) "Selected" else "Unselected"
        }, content: $content"
    }
}