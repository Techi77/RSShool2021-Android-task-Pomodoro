package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding

@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity(), TimerListener {
    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimerAdapter(this) // для отображения данных в RecyclerView
    private val timers = mutableListOf<Timer>() //переменная, в которую мы будем закидывать данные конкретного таймера
    private var nextId = 0 //для выбора определённого таймера

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        //функция по добавлению нового таймера по клику "добавить"
        binding.addNewTimerButton.setOnClickListener {
            val timerInputMinutes = binding.editTime.text.toString()
            if (timerInputMinutes.isEmpty() || timerInputMinutes.toInt() == 0) {
                Toast.makeText(applicationContext, "Введите данные!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val timerTime = timerInputMinutes.toLong() * 60 * 1000 //перевод минут в милисекунды
            timers.add(Timer(nextId++, timerTime, false, timerTime, false)) //добавление данных в массив
            timerAdapter.submitList(timers.toList()) //добавление таймера
        }
    }

    //старт работы таймера
    override fun start(id: Int) {
        timers.forEach { if (it.isStarted) it.isStarted = false} //Запущен ли какой-нибудь другой таймер? Если да - сменить на "не запущен"
        changeTimer(id, null, true) //сменить currentMs по таймеру на null, поставить, что таймер запущен
        timerAdapter.submitList(timers.toList()) // загрузить новые данные в адаптер
        timerAdapter.notifyDataSetChanged() //Он сообщает ListView , что данные были изменены; и чтобы показать новые данные, ListView должен быть перерисован.
    }

    override fun stop(id: Int, currentMs: Long) {
        changeTimer(id, currentMs, false) //сменяет статус таймера на "не запущен"
        timerAdapter.notifyDataSetChanged()
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id }) // удалить таймер
        timerAdapter.submitList(timers.toList())
        timerAdapter.notifyDataSetChanged()
    }

    //функция перехода к другому таймеру
    private fun changeTimer(id: Int, currentMs: Long?, isStarted: Boolean) {
        timers
            .find { it.id == id } //найти нужный таймер
            ?.let {
                it.currentMs = currentMs ?: it.currentMs //сменить currentMs на новые
                it.isStarted = isStarted//сменить состояние таймера на "запущен"
            }
    }
}