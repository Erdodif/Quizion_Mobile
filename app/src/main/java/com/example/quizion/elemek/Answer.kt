package com.example.quizion.elemek

class Answer(
    private var id: Int?,
    private var questionId: Int?,
    private var content: String?,
    private var right: Int?
) {
    fun getId(): Int? {
        return this.id
    }

    fun getQuestionId(): Int? {
        return this.questionId
    }

    fun getcontent(): String? {
        return this.content
    }

    fun isRight(): Boolean? {
        return this.right == 1
    }
}