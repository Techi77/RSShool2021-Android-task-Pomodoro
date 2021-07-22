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
        binding.timerTextview.text = timer.currentMs.displayTime()
        binding.customView.setPeriod(timer.initMs)
        binding.customView.setCurrent(timer.initMs-timer.currentMs)

        if (timer.isStarted) {
            startTimer(timer)
        } else {
            stopTimer()
        }
        initButtonsListeners(timer)
    }

    private fun initButtonsListeners(timer: Timer) {
        binding.startStopButton.setOnClickListener {
            if (timer.isStarted) {
                this.timer2?.cancel()
                listener.stop(timer.id, timer.currentMs)
            } else {
                listener.start(timer.id)
            }
        }
        binding.deleteButton.setOnClickListener {
            if (timer.isStarted) {
                this.timer2?.cancel()
            }
            listener.delete(timer.id)
        }
    }

    private fun startTimer(timer: Timer) {
        binding.startStopButton.text = resources.getString(R.string.stop)

        this.timer2?.cancel()
        this.timer2 = getCountDownTimer(timer)
        this.timer2?.start()

        binding.timerCardView.setCardBackgroundColor(Color.WHITE)

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer() {
        binding.startStopButton.text = resources.getString(R.string.start)

        this.timer2?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {
        return object : CountDownTimer(timer.currentMs, UNIT_ONE_SECOND) {
            val interval = UNIT_ONE_SECOND

            override fun onTick(millisUntilFinished: Long) {
                binding.customView.setPeriod(timer.initMs)
                binding.customView.setCurrent(timer.initMs-timer.currentMs)
                timer.currentMs = timer.currentMs - interval
                binding.timerTextview.text = timer.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.customView.setPeriod(timer.initMs)
                binding.customView.setCurrent(timer.currentMs)
                binding.timerTextview.text = timer.initMs.displayTime()
                timer.currentMs = timer.initMs
                timer.isStarted=false
                stopTimer()
                binding.timerCardView.setCardBackgroundColor(Color.rgb(149,52,62))
            }
        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00"
        const val UNIT_ONE_SECOND = 1000L
    }
}