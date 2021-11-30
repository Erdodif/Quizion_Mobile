package hu.petrik.quizion.adatbazis

import android.os.Bundle
import android.util.JsonReader
import java.net.HttpURLConnection
import java.io.DataOutputStream
import java.lang.Exception
import org.json.JSONObject
import android.util.Log
import hu.petrik.quizion.MainActivity
import kotlinx.coroutines.*
import java.net.URL

class SQLConnector {
    companion object {
        suspend fun apiHivas(
            method: Method,
            urlExtension: String,
            params: JSONObject? = null
        ): String? {
            Log.d("HTTP hívás", "fut a mellékszálon")
            val baseUrl = "http://10.147.20.1/api/"
            val isGet = method.params == "GET"
            var data: String?
            return withContext(Dispatchers.IO) {
                val connection =
                    URL(baseUrl + urlExtension).openConnection() as HttpURLConnection
                connection.doOutput = !isGet
                connection.setRequestProperty("Accept", "application/json")
                connection.requestMethod = method.type
                if (!isGet && params !== null) {
                    connection.setRequestProperty("Content-Type", "application/json; utf-8")
                    val out = DataOutputStream(connection.outputStream)
                    with(out) {
                        writeBytes(params.toString())
                        flush()
                        close()
                    }
                }
                try {
                    val responseCode: Int = connection.responseCode
                    data = connection.inputStream.bufferedReader().readText()
                    if (responseCode != HttpURLConnection.HTTP_OK &&
                        responseCode != HttpURLConnection.HTTP_CREATED &&
                        responseCode != HttpURLConnection.HTTP_NO_CONTENT
                    ) {
                        throw Exception("$responseCode:$data")
                    }
                    Log.d("Kérés állapota:", "Adat visszatért!")
                    Log.d("Adat:", data.toString())

                } catch (e: Exception) {
                    Log.d("Kérés állapota", "Hiba/" + e.message)
                    data = null
                } finally {
                    connection.disconnect()
                }
                return@withContext data
            }
        }
    }
}