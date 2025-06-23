package com.example.backendtest.data.model

enum class DifficultyType(val apiName: String, val displayName: String) {
    easy("easy", "Łatwy"),
    medium("medium", "Średni"),
    hard("hard", "Trudny");

    override fun toString(): String = displayName

    companion object {
        fun fromApiName(name: String): DifficultyType {
            return values().find { it.apiName.equals(name, ignoreCase = true) }
                ?: medium
        }
    }
}
