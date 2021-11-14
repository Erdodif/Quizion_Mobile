package hu.petrik.quizion.elemek

import android.util.Log
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Answer(
    private var id: Int?,
    private var questionId: Int?,
    private var content: String?,
    private var right: Int?
) {
    fun getId(): Int? {
        return this.id
    }

    fun getQuestionId(): Int? {
        return this.questionId
    }

    fun getcontent(): String? {
        return this.content
    }

    fun isRight(): Boolean? {
        return this.right == 1
    }

    companion object{
        suspend fun getAll(params: HashMap<String,Any>? = null): List<Answer>?{
            var answerList : LinkedList<Answer>?= null
            val paramJSON = JSONObject()
            paramJSON.put("table","answer")
            if(params!==null){
                for(entry in params.entries){
                    Log.d("Param ${entry.key}",entry.value.toString())
                    paramJSON.put(entry.key,entry.value)
                }
            }
            val quizJSON = SQLConnector.apiHivas(Method.READ, paramJSON)
            if (quizJSON!==null){
                val jsonContact = JSONObject(quizJSON)
                val hiba = jsonContact.getBoolean("error")
                if(hiba){
                    Log.d("Hibaállapot",hiba.toString())
                    Log.d("Hibakód",jsonContact.getString("message"))
                }
                else{
                    answerList = LinkedList<Answer>()
                    val jsonArray: JSONArray = jsonContact.getJSONArray("data")
                    val size = jsonArray.length()
                    for (i in 0 until size){
                        val elem = jsonArray.getJSONObject(i)
                        val answer = Answer(
                            elem.getInt("id"),
                            elem.getInt("question_id"),
                            elem.getString("content"),
                            elem.getInt("is_right")
                        )
                        answerList.add(answer)
                    }
                }
            }
            return answerList
        }
    }
}