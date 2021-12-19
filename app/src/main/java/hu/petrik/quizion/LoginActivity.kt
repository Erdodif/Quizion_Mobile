package hu.petrik.quizion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.elemek.ViewSwapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private var loginInProgress = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.buttonLogin.setOnClickListener {
            Toast.makeText(this, "no suspend", Toast.LENGTH_SHORT).show()
            login(
                this,
                bind.textInputUID.editText!!.text.toString(),
                bind.textInputPassword.editText!!.text.toString()
            )
        }
        bind.buttonRegister.setOnClickListener {
            ViewSwapper.swapActivity(this,RegisterActivity())
        }
    }

    fun login(context: Context,uID : String ,password: String)  {
        if(loginInProgress){
            return
        }
        loginInProgress = true
        val params = JSONObject()
        params.put("userID", uID)
        params.put("password", password)
        var result: ArrayList<String> = ArrayList()
        val run = runBlocking {
            try {
                Toast.makeText(context, "Try elindult!", Toast.LENGTH_SHORT).show()
                val run = launch {
                    result = SQLConnector.apiHivas(Method.READ, "quiz/1/questions", params)
                    Toast.makeText(context, "Adat visszatért", Toast.LENGTH_SHORT).show()
                }
                run.join()
                if (result[0].startsWith("2")) {
                    val sharedPref = context.getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    with(sharedPref.edit()) {
                        putString("Token", result[1])
                        apply()
                    }
                    ViewSwapper.swapActivity(context, QuizList())
                    Toast.makeText(context, "váltani kéne...", Toast.LENGTH_SHORT).show()
                    Log.d("Állapot", "váltani kéne")
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show()
                Log.d("Állapot", "Sikertelen bejelentkezés")
            }
            return@runBlocking
        }
        Toast.makeText(context, "Lezárult!", Toast.LENGTH_SHORT).show()
        Log.d("Állapot", "Lezárult!")
        loginInProgress = false
    }

}