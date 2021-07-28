package com.example.myapplication

interface TimerListener {

    fun start(id: Int) //функция запуска таймера

    fun stop(id: Int, currentMs: Long)//функция паузы таймера

    fun delete(id: Int)//функция удаления таймера
    fun makeFinalNotification()
}