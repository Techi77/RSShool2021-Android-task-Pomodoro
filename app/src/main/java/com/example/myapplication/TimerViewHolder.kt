package com.example.myapplication

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.StopwatchItemBinding

class TimerViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: TimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer2: CountDownTimer? = null

    fun bind(timer: Timer) {
        binding.timerTextview.text = timer.currentMs.displayTime()//записать в TextView актуальные данные через функцию displayTime(Utils)
        binding.customView.setPeriod(timer.initMs) //ввод периода для отображения customView
        binding.customView.setCurrent(timer.initMs-timer.currentMs)//ввод актуальных данных для отображения customView
        if (timer.isEnded){
            binding.timerCardView.setCardBackgroundColor(Color.rgb(149,52,62)) //задать строке тёмно-красный цвет
        }
        else{
            binding.timerCardView.setCardBackgroundColor(Color.WHITE)
        }

        if (timer.isStarted) {
            startTimer(timer) //если таймер запущен, запустить функцию "startTimer", с данными переменной timer
        } else {
            stopTimer()//иначе запустить функцию "stopTimer"
        }
        initButtonsListeners(timer)
    }

    //Функция обработки кнопки START/STOP
    private fun initButtonsListeners(timer: Timer) {
        binding.startStopButton.setOnClickListener {
            if (timer.isStarted) {
                this.timer2?.cancel() //останавливает подсчёт времени таймера
                listener.stop(timer.id, timer.currentMs) //запуск функции "стоп"
            } else {
                timer.isEnded = false //таймер закончил работу, так что задаём статус "отработал"
                listener.start(timer.id)//запуск функции "старт"
            }
        }
        binding.deleteButton.setOnClickListener {
            if (timer.isStarted) {
                this.timer2?.cancel()//останавливает подсчёт времени таймера
            }
            listener.delete(timer.id)//запуск функции "удалить"
        }
    }

    private fun startTimer(timer: Timer) {
        binding.startStopButton.text = resources.getString(R.string.stop)//поменять текст кнопки на "СТОП"

        this.timer2?.cancel()//останавливает подсчёт времени таймера
        this.timer2 = getCountDownTimer(timer) //запуск функции getCountDownTimer
        this.timer2?.start() //запуск подсчёта времени таймера

        //if (timer.isStarted) {binding.timerCardView.setCardBackgroundColor(Color.WHITE)} //установить белый цвет заливки строчки таймера

        binding.blinkingIndicator.isInvisible = false //выключить видимость blinkingIndicator
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start() //старт анимации точки
    }

    private fun stopTimer() {
        binding.startStopButton.text = resources.getString(R.string.start) //поменять текст кнопки на "СТАРТ"

        this.timer2?.cancel()//останавливает подсчёт времени таймера

        binding.blinkingIndicator.isInvisible = true//включить видимость blinkingIndicator
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()//стоп анимации точки
    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {
        return object : CountDownTimer(timer.currentMs, UNIT_ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) { //функция каждого тика таймера
                binding.customView.setPeriod(timer.initMs) //задать период таймера для customView
                binding.customView.setCurrent(timer.initMs-timer.currentMs) //задать актуальное время для customView
                timer.currentMs = timer.currentMs - UNIT_ONE_SECOND //уменьшить актуальное время на период
                binding.timerTextview.text = timer.currentMs.displayTime() //поменять текстовое отображение времени на экране
            }

            override fun onFinish() {
                binding.customView.setPeriod(timer.initMs)//задать период таймера для customView
                binding.customView.setCurrent(timer.currentMs)//задать период таймера для customView
                binding.timerTextview.text = timer.initMs.displayTime()//поменять текстовое отображение времени на экране
                timer.currentMs = timer.initMs //вернуть первоначальное значение таймера
                timer.isEnded = true //таймер закончил работу, так что задаём статус "отработал"
                timer.isStarted=false //таймер закончил работу, так что задаём статус "не запущен"
                stopTimer() //запуск функции "stopTimer"
                binding.timerCardView.setCardBackgroundColor(Color.rgb(149,52,62)) //задать строке тёмно-красный цвет
            }
        }
    }
}