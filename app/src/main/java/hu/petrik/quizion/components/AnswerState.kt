package hu.petrik.quizion.components

import hu.petrik.quizion.R

@Suppress("unused")
enum class AnswerState(
    private var stringName: String,
    var background: Int,
    var textColor: Int,
    var backgroundColor: Int
) {
    DEFAULT(
        "Default",
        R.drawable.answer,
        R.color.colorPrimary,
        R.color.textColorPrimary
    ),
    SELECTED(
        "Selected",
        R.drawable.selected_answer,
        R.color.textColorPrimary,
        R.color.colorPrimary
    ),
    CHOSEN_CORRECT(
        "Correct",
        R.drawable.chosen_good_answer,
        R.color.colorPrimaryDark,
        R.color.right_answer
    ),
    CHOSEN_INCORRECT(
        "Wrong",
        R.drawable.chosen_bad_answer,
        R.color.colorPrimaryDark,
        R.color.wrong_answer
    ),
    MISSING_CORRECT(
        "Missing",
        R.drawable.unchosen_good_answer,
        R.color.colorPrimaryDark,
        R.color.right_answer
    );

    override fun toString(): String {
        return "AnswerState: ${this.stringName}"
    }
}