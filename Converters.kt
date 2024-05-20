package ru.myitschool.lab23.core


interface Converters {

    fun <T> map(mapper: Mapper<T>): T

    interface Mapper<T> {
        fun map(base: String): T
    }

}
