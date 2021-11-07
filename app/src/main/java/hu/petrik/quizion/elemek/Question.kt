package hu.petrik.quizion.elemek

import android.util.Log
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Question(
    private var id: Int?,
    private var quizId: Int?,
    private var content: String?,
    private var rightAnswerCount: Int?,
    private var point: Int?,
) {
    fun getId(): Int? {
        return this.id
    }

    fun getQuizId(): Int? {
        return this.quizId
    }

    fun getcontent(): String? {
        return this.content
    }

    fun getRightAnswerCount(): Int? {
        return this.rightAnswerCount
    }

    fun getPoint(): Int? {
        return this.point
    }
    companion object{
        suspend fun getAll(): List<Question>?{
            var questionList : LinkedList<Question>?= null
            val paramJSON = JSONObject()
            paramJSON.put("table","question")
            Log.d("paramJSON",paramJSON.toString())
            val quizJSON = SQLConnector.apiHivas(Method.READ, paramJSON)
            if (quizJSON!==null){
                val jsonContact = JSONObject(quizJSON)
                val hiba = jsonContact.getBoolean("error")
                if(hiba){
                    Log.d("Hibaállapot",hiba.toString())
                    Log.d("Hibakód",jsonContact.getString("message"))
                }
                else{
                    questionList = LinkedList<Question>()
                    val jsonArray: JSONArray = jsonContact.getJSONArray("data")
                    val size = jsonArray.length()
                    for (i in 0 until size){
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
                }
            }
            return questionList
        }
    }
}