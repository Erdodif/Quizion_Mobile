package com.example.quizion.elemek

class Quiz(
    private var id: Int,
    private var header: String,
    private var description: String,
    private var active: Int
) {
    fun getId(): Int {
        return this.id
    }

    fun getHeader(): String {
        return this.header
    }

    fun getDescription(): String {
        return this.description
    }

    fun isActive(): Boolean {
        return this.active == 1
    }
}