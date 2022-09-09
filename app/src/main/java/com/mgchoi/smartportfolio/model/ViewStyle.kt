package com.mgchoi.smartportfolio.model

enum class ViewStyle(val rawValue: Int) {
    TIMELINE(0), MESSAGE(1), CARD(2);

    companion object {
        fun of(rawValue: Int): ViewStyle {
            return when (rawValue) {
                0 -> TIMELINE
                1 -> MESSAGE
                2 -> CARD
                else -> TIMELINE
            }
        }
    }
}