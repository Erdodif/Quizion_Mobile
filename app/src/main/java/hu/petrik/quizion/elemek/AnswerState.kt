package hu.petrik.quizion.elemek

import androidx.appcompat.content.res.AppCompatResources
import hu.petrik.quizion.R

enum class AnswerState(
    var stringName: String,
    var background: Int,
    var textColor: Int,
    var backgroundColor: Int
) {
    DEFAULT(
        "Default",
        R.drawable.answer,
        R.color.primary,
        R.color.on_primary
    ),
    SELECTED(
        "Selected",
        R.drawable.selected_answer,
        R.color.on_primary,
        R.color.primary
    ),
    CHOSEN_CORRECT(
        "Correct",
        R.drawable.chosen_good_answer,
        R.color.primary_variant,
        R.color.right_answer
    ),
    CHOSEN_INCORRECT(
        "Wrong",
        R.drawable.chosen_bad_answer,
        R.color.primary_variant,
        R.color.wrong_answer
    ),
    MISSING_CORRECT(
        "Missing",
        R.drawable.unchosen_good_answer,
        R.color.primary_variant,
        R.color.right_answer
    );

    override fun toString(): String {
        return "AnswerState: ${this.stringName}"
    }
}