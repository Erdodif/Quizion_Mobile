package hu.petrik.quizion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import hu.petrik.quizion.databinding.ActivityLeaderboardBinding
import hu.petrik.quizion.elemek.ViewBuilder
import hu.petrik.quizion.elemek.ViewSwapper

class LeaderboardActivity : AppCompatActivity() {
    lateinit var bind: ActivityLeaderboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        bind = ActivityLeaderboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        val quizId = intent.getStringExtra("quiz_id")!!.toInt()
        val result = intent.getStringExtra("result")!!
        val token = intent.getStringExtra("token")!!
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        ViewBuilder.loadLeaderboard(bind.layoutLeaderboard, bind.layoutResultSelf ,quizId,token)
        bind.buttonBack.setOnClickListener {
            ViewSwapper.swapActivity(this,QuizList(), finish = true)
        }
    }

}