package hu.petrik.quizion.elemek

import hu.petrik.quizion.adatbazis.SQLConnector.Companion.serverCall
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

@Suppress("unused")
class Quiz {
    var id: Int?
        private set
    var header: String
        private set
    var description: String
        private set
    var secondsPerQuiz: Int
        private set

    constructor(id: Int?, header: String, description: String,secondsPerQuiz: Int) {
        this.id = id
        this.header = header
        this.description = description
        this.secondsPerQuiz = secondsPerQuiz
    }

    constructor(jsonObject: JSONObject) {
        this.id = jsonObject.get("id") as Int?
        this.header = jsonObject.get("header") as String
        this.description = jsonObject.get("description") as String
        this.secondsPerQuiz = jsonObject.get("seconds_per_quiz") as Int
    }

    companion object {

        @Suppress("SpellCheckingInspection")
        suspend fun getAllActive(): ArrayList<Quiz> {
            val response = serverCall("GET", "quizes/actives")
            if (response[0].startsWith("2")) {
                val json = JSONArray(response[1])
                val list = ArrayList<Quiz>()
                for (i in 0 until json.length()) {
                    val item = json.getJSONObject(i)
                    list.add(Quiz(item))
                }
                return list
            } else {
                throw Exception("😴😫")
            }
        }

        suspend fun getById(id: Int): Quiz {
            return Quiz(JSONObject(serverCall("GET", "quiz/$id")[1]))
        }
    }
}