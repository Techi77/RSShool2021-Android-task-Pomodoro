package com.example.myapplication

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.StopwatchItemBinding

class TimerViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: TimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root), TickTackListener {

    private  var _currentTimer: Timer? = null
    private  val currentTimer: Timer get() = _currentTimer!!

    fun bind(timer: Timer) {
        if (_currentTimer != null){
            if(timer != _currentTimer) {
                _currentTimer?.isAttached = false
                _currentTimer = timer
            }
        } else {
            _currentTimer = timer
        }
        timer.listener = this
        updateFields()
        initButtonsListeners(timer)
    }

    //Функция обработки кнопки START/STOP
    private fun initButtonsListeners(timer: Timer) {
        binding.startStopButton.setOnClickListener {
            if (timer.isStarted) {

                listener.stop(timer.id, timer.currentMs) //запуск функции "стоп"

            } else {

                listener.start(timer.id)//запуск функции "старт"
            }
        }
        binding.deleteButton.setOnClickListener {
            listener.delete(timer.id)//запуск функции "удалить"
        }
    }

    override fun updateFields() {
        binding.timerTextview.text = currentTimer.currentMs.displayTime()//записать в TextView актуальные данные через функцию displayTime(Utils)
        binding.customView.setPeriod(currentTimer.initMs) //ввод периода для отображения customView
        binding.customView.setCurrent(currentTimer.initMs-currentTimer.currentMs)//ввод актуальных данных для отображения customView
        if (currentTimer.isEnded){
            binding.timerCardView.setCardBackgroundColor(Color.rgb(149,52,62)) //задать строке тёмно-красный цвет
            binding.fullCustomView.isVisible = true
        }
        else{
            binding.timerCardView.setCardBackgroundColor(Color.WHITE)
            binding.fullCustomView.isVisible = false
        }

        if (currentTimer.isStarted) {
            binding.blinkingIndicator.isInvisible = false
            (binding.blinkingIndicator.background as? AnimationDrawable)?.start()

            binding.startStopButton.text = resources.getString(R.string.stop)
        } else {
            binding.blinkingIndicator.isInvisible = true
            (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
            binding.startStopButton.text = resources.getString(R.string.start)
        }
    }

    override fun makeFinalNotification() {
        listener.makeFinalNotification()
    }

//    private fun startTimer(timer: Timer) {
//        binding.startStopButton.text = resources.getString(R.string.stop)//поменять текст кнопки на "СТОП"
//
//        this.timer2?.cancel()//останавливает подсчёт времени таймера
//        this.timer2 = getCountDownTimer(timer) //запуск функции getCountDownTimer
//        this.timer2?.start() //запуск подсчёта времени таймера
//
//         //выключить видимость blinkingIndicator
//        (binding.blinkingIndicator.background as? AnimationDrawable)?.start() //старт анимации точки
//    }
//
//    private fun stopTimer() {
//        binding.startStopButton.text = resources.getString(R.string.start) //поменять текст кнопки на "СТАРТ"
//
//        this.timer2?.cancel()//останавливает подсчёт времени таймера
//
//        binding.blinkingIndicator.isInvisible = true//включить видимость blinkingIndicator
//        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()//стоп анимации точки
//    }


}