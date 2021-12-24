package hu.petrik.quizion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.elemek.ViewSwapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

@Suppress("Deprecation")
class LoginActivity : AppCompatActivity() {
    private var loginInProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor = this.resources.getColor(R.color.primary_variant)
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
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
            ViewSwapper.swapActivity(this, RegisterActivity())
        }
    }

    private fun login(context: Context, uID: String, password: String) {
        if (loginInProgress) {
            return
        }
        loginInProgress = true
        val params = JSONObject()
        params.put("userID", uID)
        params.put("password", password)
        var result: ArrayList<String> = ArrayList()
        runBlocking {
            try {
                val run = launch {
                    result = SQLConnector.serverCall("POST", "user/login", params)
                }
                run.join()
                if (result[0].startsWith("2")) {
                    val sharedPref = context.getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    with(sharedPref.edit()) {
                        putString("Token", JSONObject(result[1]).getString("token"))
                        apply()
                    }
                    ViewSwapper.swapActivity(context, QuizList())
                    Log.d(getString(R.string.state), getString(R.string.login_successful))
                } else {
                    Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                        .show()
                    Log.d(getString(R.string.state), result[0])
                }
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                Log.d(getString(R.string.state), getString(R.string.login_failed))
            }
            return@runBlocking
        }
        loginInProgress = false
    }

}