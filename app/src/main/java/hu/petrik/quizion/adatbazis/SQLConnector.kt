package hu.petrik.quizion.adatbazis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.io.DataOutputStream
import java.lang.Exception
import org.json.JSONObject
import android.util.Log
import java.net.URL

class SQLConnector {
    companion object {
        suspend fun apiHivas(method: Method, params: JSONObject): String? {
            Log.d("HTTP hívás", "fut a mellékszálon")
            val baseUrl = "http://10.147.20.1/adatok/index.php"
            val isGet = method.params == "GET"
            var queryParams = ""
            if (isGet) {
                queryParams = "?" + params.toString()
                    .replace("\"", "")
                    .replace(":",  "=")
                    .replace("{", "")
                    .replace("}", "")
                    .replace(",", "&")
            }
            Log.d("querryparams",queryParams)
            val connection = URL(baseUrl + queryParams).openConnection() as HttpURLConnection
            connection.doOutput = !isGet
            connection.setRequestProperty("Accept", "application/json")
            if (method.type === "GET") {
                connection.requestMethod = method.type
            }
            if (!isGet) {
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                val out = DataOutputStream(connection.outputStream)
                with(out) {
                    writeBytes(params.toString())
                    flush()
                    close()
                }
            }
            var kiad: String?
            try {
                kiad = withContext(Dispatchers.IO) {
                    val responseCode: Int = connection.responseCode
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw Exception("Kapcsolati hibakód/$responseCode")
                    }
                    val data = connection.inputStream.bufferedReader().readText()
                    Log.d("Kérés állapota:", "Adat visszatért!")
                    Log.d("Adat:", data)
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
    }
}