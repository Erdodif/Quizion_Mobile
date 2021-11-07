package hu.petrik.quizion.elemek

class Question(
    private var id: Int?,
    private var quizId: Int?,
    private var content: String?,
    private var rightAnswerCount: Int?,
    private var point: Int?,
) {
    fun getId(): Int? {
        return this.id
    }

    fun getQuizId(): Int? {
        return this.quizId
    }

    fun getcontent(): String? {
        return this.content
    }

    fun getRightAnswerCount(): Int? {
        return this.rightAnswerCount
    }

    fun getPoint(): Int? {
        return this.point
    }
}