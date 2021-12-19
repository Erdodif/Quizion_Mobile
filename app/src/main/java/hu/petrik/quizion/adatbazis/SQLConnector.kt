package hu.petrik.quizion.adatbazis

import android.util.Log
import org.json.JSONObject
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.SynchronizedObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class SQLConnector {
    companion object : CoroutineScope {
        override val coroutineContext: CoroutineContext = Dispatchers.IO
        suspend fun apiHivas(
            method: Method,
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
                    setRequestProperty("Content-Type", "application/json")
                    requestMethod = method.type
                }
                val responseReader = connection.inputStream.bufferedReader()
                var line: String? = responseReader.readLine()
                val stringBuilder = StringBuilder()
                while (line != null) {
                    stringBuilder.append(line)
                    line = responseReader.readLine()
                }
                responseReader.close()
                ki.add(connection.responseCode.toString())
                ki.add(stringBuilder.toString())
            }
            catch (e:IOException){
                ki.add("404")
                ki.add("Not found")
            }
            Log.d("Visszatérés / Kód", ki[0])
            Log.d("Visszatérés / Tartalom", ki[1])
            return@withContext ki
        }
    }
}
