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
    }
}