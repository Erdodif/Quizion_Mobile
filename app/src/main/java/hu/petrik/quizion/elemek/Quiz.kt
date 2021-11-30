package hu.petrik.quizion.elemek

import android.os.Bundle
import android.util.Log
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.*

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
        suspend fun getAll(): ArrayList<Quiz> {
            val response = JSONArray(SQLConnector.apiHivas(Method.READ, "quizes"))
            val list = ArrayList<Quiz>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Quiz(item))
            }
            return list
        }

        suspend fun getAllActive(): ArrayList<Quiz> {
            val response = JSONArray(SQLConnector.apiHivas(Method.READ, "quizes/active"))
            val list = ArrayList<Quiz>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                list.add(Quiz(item))
            }
            return list
        }

        suspend fun getById(id: Int): Quiz {
            return Quiz(JSONObject(SQLConnector.apiHivas(Method.READ, "quiz/$id")!!))
        }
    }
}