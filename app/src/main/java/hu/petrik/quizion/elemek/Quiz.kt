package hu.petrik.quizion.elemek

import android.util.Log
import hu.petrik.quizion.adatbazis.SQLConnector.Companion.apiHivas
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

class Quiz {
    var id: Int?
        private set
    var header: String
        private set
    var description: String
        private set

    constructor(id: Int?, header: String, description: String, active: Boolean = false) {
        this.id = id
        this.header = header
        this.description = description
    }

    constructor(jsonObject: JSONObject) {
        this.id = jsonObject.get("id") as Int?
        this.header = jsonObject.get("header") as String
        this.description = jsonObject.get("description") as String
    }

    companion object {

        suspend fun getAllActive(): ArrayList<Quiz> {
            val response = apiHivas("GET", "quizes/actives")
            if(response[0].startsWith("2")){
                Log.d("Adat:", response[1])
                val json = JSONArray(response[1])
                val list = ArrayList<Quiz>()
                for (i in 0 until json.length()) {
                    val item = json.getJSONObject(i)
                    list.add(Quiz(item))
                }
                return list
            }
            else{
                throw Exception("😴😫")
            }
        }

        suspend fun getById(id: Int): Quiz {
            return Quiz(JSONObject(apiHivas("GET", "quiz/$id")[1]))
        }
    }
}