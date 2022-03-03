package hu.petrik.quizion.activities

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import hu.petrik.quizion.R
import hu.petrik.quizion.controllers.ViewSwapper
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.fragments.LoadingFragment
import hu.petrik.quizion.fragments.LoginFragment
import hu.petrik.quizion.fragments.LoginTokenFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("Deprecation")
class LoginActivity : AppCompatActivity() {
    lateinit var bind: ActivityLoginBinding
    var loginInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor = this.resources.getColor(R.color.colorPrimaryDark)
        this.window.navigationBarColor = this.resources.getColor(R.color.colorSecondary)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
    }

    override fun onResume() {
        super.onResume()
        selectRelevantMethod()
    }

    fun selectRelevantMethod() {
        if (intent.hasExtra("userID") && intent.hasExtra("password")) {
            val loginparams = Bundle()
            loginparams.putString("userID", intent.getStringExtra("userID"))
            loginparams.putString("password", intent.getStringExtra("password"))
            val fragment = LoginFragment()
            fragment.arguments = loginparams
            startFragment(fragment, true)
            intent.removeExtra("userID")
            intent.removeExtra("password")
            return
        }
        val tokens = getRememberedTokens()
        if (tokens !== null) {
            val fragment = LoginTokenFragment(tokens)
            startFragment(fragment, true)
        }
    }

    private fun handleSuccesfulLogin(result: ArrayList<String>, rememberLogin: Boolean) {
        val json = result[1]
        if (rememberLogin) {
            this@LoginActivity.saveRememberToken(
                JSONObject(json).getString("userName"),
                JSONObject(json).getString("remember_token")
            )
        }
        val token = JSONObject(json).getString("token")
        startFragment(LoginFragment(""), true)
        ViewSwapper.swapActivity(
            this@LoginActivity,
            QuizzesActivity(),
            Pair("Token", token),
            finish = false
        )
        Log.d(getString(R.string.state), getString(R.string.login_successful))
    }

    private fun handleDelayedError(error: String, uID: String, loginViaToken: Boolean) {
        val loginFragment = LoginFragment(uID)
        val loadingFragment = bind.fragmentLogin.getFragment<LoadingFragment>()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    loadingFragment.setErrorMessage(getString(R.string.login_failed))
                }
            }
        }, 1000)
        Log.d(getString(R.string.state), error)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (loginViaToken) {
                    loginFragment.initializerTask = {
                        loginFragment.fillFromExpiredToken(uID)
                    }
                }
                startFragment(loginFragment, true)
            }
        }, 2000)
    }

    private suspend fun getLoginResults(uID: String, password: String): ArrayList<String> {
        val params = JSONObject()
        params.put("userID", uID)
        params.put("password", password)
        return SQLConnector.serverCall("POST", "users/login", params)
    }

    private suspend fun getRememberResults(rememberToken: String): ArrayList<String> {
        val params = JSONObject()
        params.put("remember_token", rememberToken)
        return SQLConnector.serverCall("POST", "users/login", params)
    }

    fun login(
        uID: String, password: String,
        rememberLogin: Boolean = false,
        loginViaToken: Boolean = false
    ) {
        if (loginInProgress) {
            return
        }
        loginInProgress = true
        var result: ArrayList<String> = ArrayList()
        runBlocking {
            try {
                val loadingFragment = LoadingFragment()
                startFragment(loadingFragment, true, "LOADING")
                loadingFragment.setAnimationSource(R.raw.loading_3_lines_in_circle)
                val run = launch {
                    result = if (loginViaToken) {
                        getRememberResults(password)
                    } else {
                        getLoginResults(uID, password)
                    }
                }
                run.join()
                if (result[0].startsWith("2")) {
                    handleSuccesfulLogin(result, rememberLogin)
                } else {
                    handleDelayedError(result[1], uID, loginViaToken)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handleDelayedError(e.message.toString(), uID, loginViaToken)
            }
            return@runBlocking
        }
        loginInProgress = false
    }

    private fun saveRememberToken(userName: String, rememberToken: String) {
        val usersPrefs = getSharedPreferences(getString(R.string.app_users_pref_file), MODE_PRIVATE)
        usersPrefs.edit {
            putString(userName, rememberToken)
            apply()
        }
    }

    private fun getRememberedTokens(): List<Pair<String, String>>? {
        val remembered = ArrayList<Pair<String, String>>()
        val userPrefs = getSharedPreferences(getString(R.string.app_users_pref_file), MODE_PRIVATE)
        val content = userPrefs.all
        for (item in content) {
            remembered.add(Pair(item.key, item.value as String))
        }
        if (remembered.isEmpty()) {
            return null
        }
        return remembered
    }

    fun forgetRememberToken(userName: String) {
        val usersPrefs = getSharedPreferences(getString(R.string.app_users_pref_file), MODE_PRIVATE)
        usersPrefs.edit {
            remove(userName)
            apply()
        }
    }

    fun startFragment(fragment: Fragment, immediate: Boolean = false, tag: String? = null) {
        Log.d("Fragment váltás", fragment::class.simpleName.toString())
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(bind.fragmentLogin.id, fragment, tag)
        transaction.setTransition(TRANSIT_FRAGMENT_OPEN)
        transaction.commit()
        if (immediate) {
            runOnUiThread {
                supportFragmentManager.executePendingTransactions()
            }
        }
    }
}