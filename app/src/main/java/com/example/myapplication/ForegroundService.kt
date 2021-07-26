package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundService : Service() {

    private var isServiceStarted = false //флаг, определяет запущен ли сервис или нет, чтобы не стартовать повторно
    private var notificationManager: NotificationManager? = null //мы будем обращаться к NotificationManager, когда нам нужно показать нотификацию или обновить её состояние
    private var job: Job? = null //тут будет хранится Job нашей корутины, в которой мы запускаем обновление секундомера в нотификации. Мы сможен вызвать job?.cancel(), чтобы остановить корутину, когда сервис будет завершать свою работу.

    private val builder by lazy { //понадобиться нам всякий раз когда мы будем обновлять нотификацию, но некоторые значения Builder остаются неизменными. Поэтому мы создаем Builder при первом обращении к нему с этими параметрами. Теперь при каждом повторном обращении к builder он вернет нам готовую реализацию.
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Simple Timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
    }

    override fun onCreate() { //создаём экземпляр NotificationManager
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { //вызывается когда сервис запускается. Мы будем передавать параметры для запуска и остановки сервиса через Intent.
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) { //получаем данные из Intent и определяем что делаем дальше: стартуем или останавливаем сервис.
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return
                commandStart(startTime)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(startTime: Long) {
        if (isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStart()")
        try {
            moveToStartedState() //вызываем startForegroundService() или startService() в зависимости от текущего API
            startForegroundAndShowNotification() //создаем канал, если API >= Android O. Создаем нотификацию и вызываем startForeground()
            continueTimer(startTime)
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer(startTime: Long) { // продолжаем отсчитывать секундомер. Тут мы запускаем корутину, которую кэнсельнем, когда сервис будет стопаться. В корутине каждую секунду обновляем нотификацию stopForeground(true), и останавливаем сервис stopSelf()
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(
                        (startTime - System.currentTimeMillis()).displayTime()//.dropLast(3)
                    )
                )
                delay(INTERVAL)
            }
        }
    }

    private fun commandStop() { //останавливаем обновление секундомера job?.cancel(), убираем сервис из форегроунд стейта st
        if (!isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStop()")
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            Log.d("TAG", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private companion object {
        private const val INTERVAL = 1000L
    }
}