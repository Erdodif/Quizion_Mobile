@file:Suppress("BlockingMethodInNonBlockingContext")

package hu.petrik.quizion.database

import android.util.Log
import org.json.JSONObject
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext

class SQLConnector {
    companion object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO

        suspend fun serverCall(
            method: String,
            urlExtension: String,
            params: JSONObject? = null,
            token: String? = null
        ): ArrayList<String> = withContext(Dispatchers.IO) {
            val ki = ArrayList<String>()
            val url = URL("http://10.147.20.5/api/$urlExtension")
            try {
                val connection = url.openConnection() as HttpURLConnection
                with(connection) {
                    if (token !== null) {
                        setRequestProperty("Authorization", "Bearer $token")
                    }
                    instanceFollowRedirects = false
                    setRequestProperty("charset", "utf-8")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 5000
                    requestMethod = method
                    if (params != null) {
                        setRequestProperty("Content-Type", "application/json")
                        connection.doOutput = true
                        try {
                            val outputStream = DataOutputStream(connection.outputStream)
                            outputStream.write(params.toString().toByteArray())
                            outputStream.flush()
                            outputStream.close()
                        } catch (exception: Exception) {
                            Log.d("Error", exception.toString())
                        }
                    } else {
                        connection.doOutput = false
                    }
                }
                val responseReader = if (connection.responseCode > 399) {
                    connection.errorStream.bufferedReader()
                } else {
                    connection.inputStream.bufferedReader()
                }
                val content: String = responseReader.readText()
                responseReader.close()
                ki.add(connection.responseCode.toString())
                ki.add(content)
                connection.disconnect()
            } catch (e: IOException) {
                Log.d("Error", e.toString())
                if (ki.count() == 0) {
                    ki.add(404.toString())
                }
                if (ki.count() == 1) {
                    ki.add("Not found!")
                }
            }
            Log.d("Response code", ki[0])
            Log.d("Response content", ki[1])
            return@withContext ki
        }
    }
}
