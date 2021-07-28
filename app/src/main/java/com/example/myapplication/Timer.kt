package com.example.myapplication

import android.os.CountDownTimer

data class Timer(
    val id: Int, //id таймера
    var currentMs: Long, //сколько осталось времени таймера
    private val _isStarted: Boolean, //работает ли таймер? да/нет
    val initMs: Long, //начальное значение таймера
    var isEnded: Boolean, //завершил ли работу таймер
) {

    var isAttached = false
    lateinit var listener: TickTackListener
    var isStarted = _isStarted
        set(isSt) {
            field = isSt
            if (field) start()
            else stop()
        }

    private var countDownT: CountDownTimer? = null


    private fun start() {
        countDownT = getCountDownTimer()
        countDownT?.start()
    }

    private fun stop() {
        countDownT?.cancel()
    }

    private fun getCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(currentMs, UNIT_ONE_HUNDRED_MILLISECOND) {

            override fun onTick(millisUntilFinished: Long) { //функция каждого тика таймера
                currentMs = millisUntilFinished
                listener.updateFields()
            }

            override fun onFinish() {

                isStarted = false
                isEnded = true
                listener.updateFields()
                listener.makeFinalNotification()
            }
        }
    }
}