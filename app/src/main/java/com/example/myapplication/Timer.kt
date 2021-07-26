package com.example.myapplication

data class Timer(
    val id: Int, //id таймера
    var currentMs: Long, //сколько осталось времени таймера
    var isStarted: Boolean, //работает ли таймер? да/нет
    val initMs: Long, //начальное значение таймера
    var isEnded: Boolean, //завершил ли работу таймер
)