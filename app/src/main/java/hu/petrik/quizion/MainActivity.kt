package hu.petrik.quizion

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.petrik.quizion.adatbazis.SQLConnector
import hu.petrik.quizion.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO TELJES REMAKE A BACKEND-NEK MEGFELELŐEN
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
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
}