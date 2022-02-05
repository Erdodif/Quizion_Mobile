package hu.petrik.quizion.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.fragment.app.findFragment
import hu.petrik.quizion.R
import hu.petrik.quizion.components.ViewSwapper
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.fragments.LoadingFragment
import hu.petrik.quizion.fragments.LoginFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("Deprecation")
class LoginActivity : AppCompatActivity() {
    lateinit var bind:ActivityLoginBinding
    var loginInProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor = this.resources.getColor(R.color.primary_variant)
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
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
        }
    }
    fun login(uID: String, password: String) {
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
                val loginFragment = bind.fragmentLogin.getFragment<LoginFragment>()
                val loadingFragment = LoadingFragment()
                var transaction = supportFragmentManager.beginTransaction()
                transaction.replace(bind.fragmentLogin.id, loadingFragment,"LOADING")
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.commit()
                supportFragmentManager.executePendingTransactions()
                loadingFragment.setAnimationSource(R.raw.loading_3_lines_in_circle)
                val run = launch {
                    result = SQLConnector.serverCall("POST", "users/login", params)
                }
                run.join()
                if (result[0].startsWith("2")) {
                    val remember_token = JSONObject(result[1]).getString("remember_token")
                    val uName = JSONObject(result[1]).getString("userName")
                    val token = JSONObject(result[1]).getString("token")
                    transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(bind.fragmentLogin.id, LoginFragment())
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction.commit()
                    supportFragmentManager.executePendingTransactions()
                    ViewSwapper.swapActivity(
                        this@LoginActivity,
                        QuizzesActivity(),
                        Pair("Token", token),
                        finish = false
                    )
                    Log.d(getString(R.string.state), getString(R.string.login_successful))
                } else {
                    Timer().schedule(object: TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                loadingFragment.setErrorMessage(getString(R.string.login_failed))
                            }
                        }
                    },1000)
                    Log.d(getString(R.string.state), result[0])
                    Timer().schedule(object: TimerTask() {
                        override fun run() {
                            transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(bind.fragmentLogin.id, loginFragment)
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            transaction.commit()
                        }
                    },2000)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                Log.d(getString(R.string.state), getString(R.string.login_failed))
                Timer().schedule(object: TimerTask() {
                    override fun run() {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(bind.fragmentLogin.id, LoginFragment())
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        transaction.commit()
                    }
                },2000)
            }
            return@runBlocking
        }
        loginInProgress = false
    }
}