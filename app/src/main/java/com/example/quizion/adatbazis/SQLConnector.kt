package com.example.quizion.adatbazis

import com.example.quizion.elemek.Question
import com.example.quizion.elemek.Answer
import com.example.quizion.elemek.Quiz

import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.lang.Exception
import org.json.JSONObject
import org.json.JSONArray
import android.util.Log
import java.io.DataOutputStream
import java.net.URL
import java.util.*

class SQLConnector {
    companion object {
        private var threadLocked = false

        public fun apiHivas(method: Method, params: JSONObject): String? {
            /*
            if (threadLocked) {
                Log.d("Figyelmeztetés", "Szál foglalva!")
                return null
            }*/
            threadLocked = true
            Log.d("HTTP hívás", "fut a mellékszálon")
            val baseUrl = "http://10.147.20.1/adatok/index.php"
            val isGet = method.params == "GET"
            var kiParams = ""
            if (isGet){
                kiParams = params.toString()
                    .replace("\"","")
                    .replace(':','=')
                    .replace("{","")
                    .replace("}","")
            }
            val connection = URL(
                baseUrl + if (isGet) "?$kiParams" else ""
            ).openConnection() as HttpURLConnection
            connection.doOutput = !isGet
            connection.requestMethod = method.type
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
            if (connection.doOutput) {
                val out = DataOutputStream(connection.getOutputStream())
                out.writeBytes(params.toString())
                out.flush()
                out.close()
            }
            var kiad: String?
            try {
                val responseCode: Int = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Kapcsolati hibakód/" + responseCode)
                }
                val data = connection.inputStream.bufferedReader().readText()
                Log.d("Kérés állapota:", "Adat visszatért!")
                kiad = data
            } catch (e: Exception) {
                Log.d("Kérés állapota", "Hiba/" + e.message.toString())
                kiad = null
            } finally {
                connection.disconnect()
                threadLocked = false
            }
            return kiad
        }

        /*
                private fun getQuestionAll() :List<Question>{
                    val quizJSON = apiHivas("method=read&table=question")
                    val jsonContact = JSONObject(quizJSON!!)
                    val jsonArray: JSONArray = jsonContact.getJSONArray("data")
                    val size = jsonArray.length()
                    val questionList = LinkedList<Question>()
                    for (i in 0 until size) {
                        val elem = jsonArray.getJSONObject(i)
                        val question = Question(
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

                private fun getAnswerAll() :List<Answer>{
                    val quizJSON = apiHivas("table=answer")
                    val jsonContact = JSONObject(quizJSON!!)
                    val jsonArray: JSONArray = jsonContact.getJSONArray("data")
                    val size = jsonArray.length()
                    val answerList = LinkedList<Answer>()
                    for (i in 0 until size) {
                        val elem = jsonArray.getJSONObject(i)
                        val answer = Answer(
                            elem.getInt("id"),
                            elem.getInt("question_id"),
                            elem.getString("content"),
                            elem.getInt("is_right")
                        )
                        answerList.add(answer)
                    }
                    return answerList
                }
        */
        fun init() {
        }


    }
}