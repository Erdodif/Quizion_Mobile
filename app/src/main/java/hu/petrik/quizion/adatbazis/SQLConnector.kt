package hu.petrik.quizion.adatbazis

import android.util.Log
import org.json.JSONObject
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.SynchronizedObject
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class SQLConnector {
    companion object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO
        suspend fun apiHivas(
            method: String,
            urlExtension: String,
            params: JSONObject? = null,
            token: String? = null
        ): ArrayList<String> = withContext(Dispatchers.IO) {
            val ki= ArrayList<String>()
            val url = URL("http://10.147.20.1/api/$urlExtension")
            try {
                val connection = url.openConnection() as HttpURLConnection
                with(connection) {
                    if (token !== null) {
                        setRequestProperty("Authorization", "Bearer $token")
                    }
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
                            Log.d("Küldési hiba", exception.toString())
                        }
                    } else {
                        connection.doOutput = false
                    }
                }
                val responseReader :BufferedReader
                if(connection.responseCode > 399){
                    responseReader = connection.errorStream.bufferedReader()
                }
                else{
                    responseReader = connection.inputStream.bufferedReader()
                }
                val content: String = responseReader.readText()
                responseReader.close()
                ki.add(connection.responseCode.toString())
                ki.add(content)
                connection.disconnect()
            }
            catch (e:IOException){
                Log.d("Error", e.toString())
                if (ki.count() ==0){
                    ki.add(404.toString())
                }
                if (ki.count() ==1){
                    ki.add("Not found!")
                }
            }
            Log.d("Visszatérés / Kód", ki[0])
            Log.d("Visszatérés / Tartalom", ki[1])
            return@withContext ki
        }
    }
}
