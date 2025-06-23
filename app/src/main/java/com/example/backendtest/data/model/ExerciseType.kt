package com.example.backendtest.data.model

enum class ExerciseType(val apiName: String, val displayName: String) {
    cardio("cardio", "Kardio"),
    strength("strength", "Siłowe"),
    flexibility("flexibility", "Rozciąganie"),
    balance("balance", "Równowaga");

    override fun toString(): String = displayName

    companion object {
        fun fromApiName(name: String): ExerciseType {
            return values().find { it.apiName.equals(name, ignoreCase = true) }
                ?: strength // fallback lub rzuć wyjątek jeśli wolisz
        }
    }
}
