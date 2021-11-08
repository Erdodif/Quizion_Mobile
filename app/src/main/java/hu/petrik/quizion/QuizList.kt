package hu.petrik.quizion

import android.os.Bundle
import android.content.Intent
import hu.petrik.quizion.elemek.Quiz
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.databinding.ActivityQuizListBinding

class QuizList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(bind.root)
        //ideiglenes
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}