package hu.petrik.quizion

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.buttonTovabb.setOnClickListener {
            val intent = Intent(this, QuizList::class.java)
            startActivity(intent)
            finish()
        }
    }
}