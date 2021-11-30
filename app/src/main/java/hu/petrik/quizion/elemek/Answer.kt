package hu.petrik.quizion.elemek

import android.util.Log
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class Answer {
    var id: Int?
        private set
    var questionId: Int
        private set
    var content: String
        private set

    constructor(
        id: Int?,
        quiz_id: Int,
        content: String,
        right: Boolean
    ) {
        this.id = id
        this.questionId = quiz_id
        this.content = content
    }

    constructor(jsonObject: JSONObject) {
        this.id = jsonObject.get("id") as Int?
        this.questionId = jsonObject.getInt("question_id")
        this.content = jsonObject.getString("content")
    }

    companion object {
        suspend fun getAll(): ArrayList<Answer> {
            val response = JSONArray(SQLConnector.apiHivas(Method.READ, "answers"))
            val list = ArrayList<Answer>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Answer(item))
            }
            return list
        }

        suspend fun getById(id: Int): Quiz {
            return Quiz(JSONObject(SQLConnector.apiHivas(Method.READ, "answer/$id")!!))
        }
    }
}