package com.example.quizion.adatbazis

import java.net.HttpURLConnection
import java.lang.Exception
import org.json.JSONObject
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.net.URL

class SQLConnector {
    companion object {
        private var threadLocked = false

        suspend fun apiHivas(method: Method, params: JSONObject): String? {
            Log.d("HTTP hívás", "fut a mellékszálon")
            val baseUrl = "http://10.147.20.1/adatok/index.php"
            val isGet = method.params == "GET"
            var queryParams = ""
            if (isGet) {
                queryParams = "?" + params.toString()
                    .replace("\"", "")
                    .replace(':', '=')
                    .replace("{", "")
                    .replace("}", "")
            }
            val connection = URL(baseUrl + queryParams).openConnection() as HttpURLConnection
            connection.doOutput = !isGet
            connection.setRequestProperty("Accept", "application/json")
            if (method.type === "GET") {
                connection.requestMethod = method.type
            }
            if (!isGet) {
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                val out = DataOutputStream(connection.outputStream)
                out.writeBytes(params.toString())
                out.flush()
                out.close()
            }
            var kiad: String?
            try {
                kiad = withContext(Dispatchers.IO) {

                    val responseCode: Int = connection.responseCode
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw Exception("Kapcsolati hibakód/" + responseCode)
                    }
                    val data = connection.inputStream.bufferedReader().readText()
                    Log.d("Kérés állapota:", "Adat visszatért!")
                    return@withContext data
                }

            } catch (e: Exception) {
                Log.d("Kérés állapota", "Hiba/" + e.message)
                kiad = null
            } finally {
                connection.disconnect()
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