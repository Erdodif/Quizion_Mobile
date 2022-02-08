package hu.petrik.quizion.activities

import hu.petrik.quizion.databinding.ActivityQuizListBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import hu.petrik.quizion.fragments.LeaderboardFragment
import hu.petrik.quizion.R

@Suppress("MemberVisibilityCanBePrivate")
class QuizzesActivity : AppCompatActivity() {

    lateinit var token: String

    @Suppress("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.navigationBarColor = this.resources.getColor(R.color.colorSecondary)
        super.onCreate(savedInstanceState)
        val bind = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(bind.root)
        this.token = intent.getStringExtra("Token")!!
        if (intent.hasExtra("quiz_id")) {
            val quizId = intent.getStringExtra("quiz_id")!!
            if (intent.hasExtra("result")) {
                val result = intent.getStringExtra("result")!!
                val leaderboardParams = Bundle()
                leaderboardParams.putString("quiz_id", quizId)
                leaderboardParams.putString("result", result)
                val fragment = LeaderboardFragment()
                fragment.arguments = leaderboardParams
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(bind.fragmentQuizList.id, fragment)
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.commit()
            } else {
                //TODO Quiz előnézete
            }
        }
    }

}