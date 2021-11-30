package hu.petrik.quizion.elemek

import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class Question {
    var id: Int?
        private set
    var quiz_id: Int
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
        this.quiz_id = quiz_id
        this.noRightAnswers = noRightAnswers
        this.content = content
        this.point = point
    }

    constructor(jsonObject: JSONObject) {
        this.id = jsonObject.get("id") as Int?
        this.quiz_id = jsonObject.getInt("quiz_id")
        this.content = jsonObject.getString("content")
        this.noRightAnswers = jsonObject.getInt("no_right_answers")
        this.point = jsonObject.getInt("point")
    }


    companion object {
        suspend fun getAll(): ArrayList<Question> {
            val response = JSONArray(SQLConnector.apiHivas(Method.READ, "questions"))
            val list = ArrayList<Question>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Question(item))
            }
            return list
        }

        suspend fun getById(id: Int): Quiz {
            return Quiz(JSONObject(SQLConnector.apiHivas(Method.READ, "question/$id")!!))
        }
    }
}