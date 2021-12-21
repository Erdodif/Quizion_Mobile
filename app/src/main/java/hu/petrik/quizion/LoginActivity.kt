package hu.petrik.quizion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.elemek.ViewSwapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private var loginInProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        val bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.buttonLogin.setOnClickListener {
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
                val run = launch {
                    result = SQLConnector.apiHivas("POST", "user/login", params)
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
                    Log.d("Állapot", "váltani kéne")
                }
                else{
                    Toast.makeText(context, "Sikertelen bejelentkezés", Toast.LENGTH_SHORT).show()
                    Log.d("Állapot", result[0])
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show()
                Log.d("Állapot", "Sikertelen bejelentkezés")
            }
            return@runBlocking
        }
        Log.d("Állapot", "Lezárult!")
        loginInProgress = false
    }

}