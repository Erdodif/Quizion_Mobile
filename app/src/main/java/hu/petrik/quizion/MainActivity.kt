package hu.petrik.quizion

import android.app.Activity
import hu.petrik.quizion.elemek.Question
import hu.petrik.quizion.elemek.Answer
import hu.petrik.quizion.elemek.Quiz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import hu.petrik.quizion.elemek.ViewBuilder
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.adatbazis.Method
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import hu.petrik.quizion.elemek.ViewBuilder.Companion.kerdesBetolt
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO TELJES REMAKE A BACKEND-NEK MEGFELELŐEN
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        val view = bind.root
        setContentView(view)
        val id = intent.getIntExtra("id", -1)
        /*try {*/
            Log.d("id", id.toString())
            game = Game.newGame(id)
            game.play(bind)
        /*} catch (e: Exception) {
            kerdesBetolt(bind.textViewKerdes!!, e.toString())
        }*/
    }

    fun jumpOnNext(rightId: Int) {
        var joe = false
        suspend {
            joe = JSONObject(
                SQLConnector.apiHivas(Method.READ,"pick/answer/$rightId")[1]
            ).get("us_rigth") as Boolean
        }
        if(joe){
            Toast.makeText(this, "Jó 😁", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Nem jó 😫", Toast.LENGTH_SHORT).show()
        }
        if (game.hasNext()) {
            game.toNext()
            game.play(bind, game.actual)
        } else {
            endingScreen()
        }
    }

    fun endingScreen() {
        TODO()
    }
}