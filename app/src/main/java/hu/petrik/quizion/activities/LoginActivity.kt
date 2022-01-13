package hu.petrik.quizion.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.fragment.app.commit
import hu.petrik.quizion.R
import hu.petrik.quizion.databinding.ActivityLoginBinding
import hu.petrik.quizion.fragments.LoginFragment

@Suppress("Deprecation")
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor = this.resources.getColor(R.color.primary_variant)
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        val bind = ActivityLoginBinding.inflate(layoutInflater)
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
}