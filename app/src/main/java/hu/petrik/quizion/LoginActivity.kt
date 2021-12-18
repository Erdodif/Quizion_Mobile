package hu.petrik.quizion

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.elemek.ViewSwapper
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.buttonLogin.setOnClickListener {
            val uID = bind.textInputUID.editText!!.text
            val password = bind.textInputPassword.editText!!.text
            val params = JSONObject()
            params.put("userID", uID)
            params.put("password", password)
                try {
                    Toast.makeText(this, "Try elindult!", Toast.LENGTH_SHORT).show()
                    val result: ArrayList<String>
                    val megy = runBlocking {
                        result = SQLConnector.apiHivas(Method.CREATE, "user/login", params)
                    }
                    if (result[0].startsWith("2")){
                        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key),
                            Context.MODE_PRIVATE)
                        with(sharedPref.edit()){
                            putString("Token",result[1])
                            apply()
                        }
                        ViewSwapper.swapActivity(this, QuizList())
                        Toast.makeText(this, "váltani kéne...", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}