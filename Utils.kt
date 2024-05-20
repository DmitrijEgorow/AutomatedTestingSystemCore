package ru.myitschool.lab23.core

class Utils {

    fun checkValidDate(sendAtCanonical: String, sendAt: String): Boolean {
        val month = sendAt.substring(5, 7) == sendAtCanonical.substring(5, 7)
        val day = sendAt.substring(8, 10) == sendAtCanonical.substring(8, 10)
        val hour = sendAt.substring(11, 13) == sendAtCanonical.substring(11, 13)
        val minutes = (sendAt.substring(14, 16) == sendAtCanonical.substring(14, 16)) ||
                (
                        (
                                sendAt.substring(14, 16)
                                    .toInt() + 1
                                ).toString() == sendAtCanonical.substring(
                            14,
                            16,
                        )
                        )
        return month && day && hour && minutes
    }

    fun getRandomString(length: Int, numbersAllowed: Boolean = true): String {
        val allowedChars = ('A'..'Z') +
                if (numbersAllowed) {
                    ('a'..'z') + ('0'..'9')
                } else {
                    ('a'..'z')
                }
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
