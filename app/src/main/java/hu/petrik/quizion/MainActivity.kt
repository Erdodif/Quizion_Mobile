package hu.petrik.quizion

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.AnswerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("Typo","unused")
class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    lateinit var game: Game
    private var buttonStateSend = true

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.navigationBarColor = getColor(R.color.primary)
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
                game = Game.newGame(id, token)
                bind.buttonSend!!.setOnClickListener {
                    this@MainActivity.hideNextButton()
                    if (buttonStateSend){
                        with(this@MainActivity){
                            game.sendResults(bind)
                            toogleNextButton()
                            showNextButton()
                        }
                    }
                    else{
                        with(this@MainActivity){
                            game.loadCurrent(bind)
                            toogleNextButton()
                        }
                    }
                }
            }.join()
            game.play(bind)
        }
    }

    fun showNextButton() {
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

    fun hideNextButton() {
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

    fun toogleNextButton(){
        this.showNextButton()
        val button = bind.buttonSend!! as MaterialButton
        if(buttonStateSend){
            button.text = this.getString(R.string.next)
            val lp = button.layoutParams as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,1)
            lp.removeRule(RelativeLayout.CENTER_IN_PARENT)
            button.layoutParams = lp
        }
        else{
            button.text = this.getString(R.string.send)
            val lp = button.layoutParams as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,1)
            lp.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
            button.layoutParams = lp
        }
        buttonStateSend = !buttonStateSend
    }

    fun setAnswerState(id: Int, state: AnswerState) {
        val button = this.findViewById<MaterialButton>(id)
        button.background = AppCompatResources.getDrawable(this, state.background)
        button.setTextColor(getColor(state.textColor))
        button.backgroundTintList = ContextCompat.getColorStateList(this,state.backgroundColor)
        Log.d("Answer_state set", state.toString())
    }


    fun setCompletionLimit(count: Int) {
        bind.progressCompletion!!.max = count
    }

    fun setCompletionState(state:Int) {
        bind.progressCompletion!!.progress = state
    }

}