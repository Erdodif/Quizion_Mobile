package hu.petrik.quizion.elemek

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class ViewSwapper {
    companion object{
        fun swapActivity (from : Context, to: Activity){
            val intent = Intent(from, to::class.java)
            startActivity(from,intent,null)
            (from as Activity).finish()
        }
    }
}