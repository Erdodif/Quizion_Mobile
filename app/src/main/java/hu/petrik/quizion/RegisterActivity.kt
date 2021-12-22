package hu.petrik.quizion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.petrik.quizion.databinding.ActivityRegisterBinding
import hu.petrik.quizion.elemek.ViewSwapper

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
        super.onCreate(savedInstanceState)
        val bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.buttonBack.setOnClickListener {
            ViewSwapper.swapActivity(this,LoginActivity())
        }
        bind.buttonRegister.setOnClickListener {
            val uname = bind.textInputUserName.editText!!.text.toString()
            val email = bind.textInputEmail.editText!!.text.toString()
            val password = bind.textInputPassword.editText!!.text.toString()
            val passwordAgain = bind.textInputPasswordAgain.editText!!.text.toString()
            if (password != passwordAgain){
                bind.textInputPasswordAgain.error = "A két jelszó nem egyezik!"
            }
            val passwordErrors = ArrayList<String>()
            if (password.length<8){
                passwordErrors.add("")
            }
        }
    }
}