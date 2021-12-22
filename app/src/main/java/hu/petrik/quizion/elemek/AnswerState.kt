package hu.petrik.quizion.elemek

import androidx.appcompat.content.res.AppCompatResources
import hu.petrik.quizion.R

enum class AnswerState(var background:Int,var textColor:Int) {
    DEFAULT(R.drawable.answer,R.color.primary),
    SELECTED(R.drawable.selected_answer,R.color.on_primary),
    CHOSEN_CORRECT(R.drawable.chosen_good_answer,R.color.primary_variant),
    CHOSEN_INCORRECT(R.drawable.chosen_bad_answer,R.color.primary_variant),
    MISSING_CORRECT(R.drawable.unchosen_good_answer,R.color.primary_variant)
}