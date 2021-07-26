package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.myapplication.databinding.StopwatchItemBinding

class TimerAdapter(
    private val listener: TimerListener
): ListAdapter<Timer, TimerViewHolder>(itemComparator) {

    //В onCreateViewHolder инфлейтим View и возвращаем созданный ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater, parent, false)
        return TimerViewHolder(binding, listener, binding.root.resources)
    }

    //вызывается в момент создания айтема, в моменты пересоздания (например, айтем вышел
    // за пределы экрана, затем вернулся) и в моменты обновления айтемов (этим у нас занимается DiffUtil)
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position)) //для конкретного ViewHolder обновляем параметры
    }

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Timer>() {

            override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted
            }

            override fun getChangePayload(oldItem: Timer, newItem: Timer): Any = Any()
        }
    }
}