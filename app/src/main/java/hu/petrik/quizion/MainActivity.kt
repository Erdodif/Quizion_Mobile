package hu.petrik.quizion

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.animate
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.AnswerState
import hu.petrik.quizion.elemek.ViewBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.time.Duration

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO TELJES REMAKE A BACKEND-NEK MEGFELELŐEN
        this.window.navigationBarColor = this.resources.getColor(R.color.primary)
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        val id = intent.getIntExtra("id", -1)
        Log.d("id", id.toString())
        val token = intent.getStringExtra("token")!!
        Log.d("token", token)
        runBlocking {
            launch {
                game = Game.newGame(id,token)
            }.join()
            game.play(bind)
        }
    }

    fun jumpOnNext(rightId: Int) {
        TODO()
        /*
        var joe = false
        suspend {
            joe = JSONObject(
                SQLConnector.apiHivas("POST","play/${game.quiz.id}",)[1]
            ).get("us_rigth") as Boolean
        }
        if(joe){
            Toast.makeText(this, "Jó 😁", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Nem jó 😫", Toast.LENGTH_SHORT).show()
        }*/
    }

    fun endingScreen() {
        TODO()
    }

    fun showNextButton(){
        bind.layoutNext!!.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate().alpha(1f).setDuration(300).setListener(null)
        }
        bind.layoutNext!!.animate()
            .alpha(1f)
            .setDuration(300.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    bind.layoutNext!!.visibility = View.VISIBLE
                }
            })
    }

    fun hideNextButton(){
        bind.layoutNext!!.apply {
            visibility = View.VISIBLE
            alpha = 1f
            animate().alpha(0f).setDuration(200).setListener(null)
        }
        bind.layoutNext!!.animate()
            .alpha(0f)
            .setDuration(200.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    bind.layoutNext!!.visibility = View.GONE
                }
            })
    }

    fun setAnswerState(id:Int, state:AnswerState){
        val button = this.findViewById<MaterialButton>(id)
        button.background = AppCompatResources.getDrawable(this,state.background)
        button.setTextColor(AppCompatResources.getColorStateList(this,state.textColor))
    }


    fun setCompletionLimit(count:Int){
        bind.progressCompletion!!.max = count
    }

    fun incrementCompletion(){
        bind.progressCompletion!!.progress++
    }

}