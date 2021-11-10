package hu.petrik.quizion.elemek

import android.util.Log
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.*

class Quiz(
    private var id: Int,
    private var header: String,
    private var description: String,
    private var active: Int
) {
    fun getId(): Int {
        return this.id
    }

    fun getHeader(): String {
        return this.header
    }

    fun getDescription(): String {
        return this.description
    }

    fun isActive(): Boolean {
        return this.active == 1
    }

    companion object {
        suspend fun getAll(): List<Quiz>? {
            var quizList: LinkedList<Quiz>? = null
            val paramJSON = JSONObject()
            paramJSON.put("table", "quiz")
            Log.d("paramJSON", paramJSON.toString())
            val quizJSON = SQLConnector.apiHivas(Method.READ, paramJSON)
            if (quizJSON !== null) {
                val jsonContact = JSONObject(quizJSON)
                val hiba = jsonContact.getBoolean("error")
                if (hiba) {
                    Log.d("Hibaállapot", hiba.toString())
                    Log.d("Hibakód", jsonContact.getString("message"))
                } else {
                    quizList = LinkedList<Quiz>()
                    val jsonArray: JSONArray = jsonContact.getJSONArray("data")
                    val size = jsonArray.length()
                    for (i in 0 until size) {
                        val elem = jsonArray.getJSONObject(i)
                        val quiz = Quiz(
                            elem.getInt("id"),
                            elem.getString("header"),
                            elem.getString("description"),
                            elem.getInt("active")
                        )
                        quizList.add(quiz)
                    }
                }
            }
            return quizList
        }
    }
}