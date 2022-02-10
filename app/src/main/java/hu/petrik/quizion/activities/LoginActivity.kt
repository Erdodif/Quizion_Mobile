package hu.petrik.quizion.activities

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import hu.petrik.quizion.R
import hu.petrik.quizion.components.ViewSwapper
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
        if (intent.hasExtra("userID") && intent.hasExtra("password")) {
            val loginparams = Bundle()
            loginparams.putString("userID", intent.getStringExtra("userID"))
            loginparams.putString("password", intent.getStringExtra("password"))
            val fragment = LoginFragment()
            fragment.arguments = loginparams
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(bind.fragmentLogin.id, fragment)
            transaction.setTransition(TRANSIT_FRAGMENT_OPEN)
            transaction.commit()
            return
        }
        saveRememberToken("erdodif","xd")
        fragmentManager.executePendingTransactions()
        val tokens = getRememberedTokens()
        if(tokens !== null){
            val fragment = LoginTokenFragment(tokens)
            startFragment(LoadingFragment(),true)
            //startFragment(fragment,true)
        }
    }

    fun login(uID: String, password: String, rememberLogin: Boolean = false,loginViaToken:Boolean = false) {
        if (loginInProgress) {
            return
        }
        loginInProgress = true
        val params = JSONObject()
        params.put("userID", uID)
        if(loginViaToken){
            params.put("remember_token", password)
        }else{
            params.put("password", password)
        }
        var result: ArrayList<String> = ArrayList()
        runBlocking {
            try {
                val loginFragment = bind.fragmentLogin.getFragment<LoginFragment>()
                val loadingFragment = LoadingFragment()
                startFragment(loadingFragment,true,"LOADING")
                loadingFragment.setAnimationSource(R.raw.loading_3_lines_in_circle)
                val run = launch {
                    result = SQLConnector.serverCall("POST", "users/login", params)
                }
                run.join()
                if (result[0].startsWith("2")) {
                    val json = result[1]
                    if (rememberLogin){
                        this@LoginActivity.saveRememberToken(
                            JSONObject(json).getString("userName"),
                            JSONObject(json).getString("remember_token")
                        )
                    }
                    val token = JSONObject(json).getString("token")
                    startFragment(LoginFragment(),true)
                    ViewSwapper.swapActivity(
                        this@LoginActivity,
                        QuizzesActivity(),
                        Pair("Token", token),
                        finish = false
                    )
                    Log.d(getString(R.string.state), getString(R.string.login_successful))
                } else {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                loadingFragment.setErrorMessage(getString(R.string.login_failed))
                            }
                        }
                    }, 1000)
                    Log.d(getString(R.string.state), result[0])
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            startFragment(loginFragment,true)
                            if(loginViaToken){
                                loginFragment.fillFromExpiredToken(uID)
                            }
                        }
                    }, 2000)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(getString(R.string.state), getString(R.string.login_failed))
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        startFragment(LoginFragment())
                    }
                }, 2000)
            }
            return@runBlocking
        }
        loginInProgress = false
    }

    private fun saveRememberToken(userName: String, rememberToken: String) {
        val usersPrefs = getSharedPreferences(getString(R.string.app_users_pref_file), MODE_PRIVATE)
        usersPrefs.edit {
            putString(userName,rememberToken)
            apply()
        }
    }
    private fun getRememberedTokens():List<Pair<String, String>>?{
        val remembered = ArrayList<Pair<String, String>>()
        val userPrefs = getSharedPreferences(getString(R.string.app_users_pref_file), MODE_PRIVATE)
        val content = userPrefs.all
        for(item in content){
            Log.d("RememberToken", "${item.key}:${item.value}")
            remembered.add(Pair(item.key,item.value as String))
        }
        if (remembered.isEmpty()){
            return null
        }
        return remembered
    }

    fun startFragment(fragment: Fragment, immediate:Boolean = false, tag:String? = null){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(bind.fragmentLogin.id, fragment,tag)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.commit()
        if (immediate){
            runOnUiThread {
                supportFragmentManager.executePendingTransactions()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val remembered = getRememberedTokens()
        startFragment(LoginFragment(),true)
    }
}