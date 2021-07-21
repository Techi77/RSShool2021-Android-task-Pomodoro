package com.example.myapplication

data class Timer(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    val initMs: Long,
    var isAlarm: Boolean
)