package hu.petrik.quizion.elemek

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class ViewSwapper {
    companion object {
        fun swapActivity(from: Context, to: Activity, vararg extras:Pair<String,String>,finish:Boolean = true) {
            val intent = Intent(from, to::class.java)
            if (extras.isNotEmpty()){
                for(extra in extras){
                    intent.putExtra(extra.first,extra.second)
                }
            }
            startActivity(from, intent, null)
            if (finish){
                (from as Activity).finish()
            }
        }
    }
}