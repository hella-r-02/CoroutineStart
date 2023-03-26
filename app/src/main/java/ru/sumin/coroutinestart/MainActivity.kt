package ru.sumin.coroutinestart

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import ru.sumin.coroutinestart.databinding.ActivityMainBinding
import kotlin.concurrent.thread

//Handler - для обменов данными меду потоками
//Looper - очередь сообщений
//Handler - deprecated (при его создании не указывается явно в каком потоке будет обрабатываться сообщение =>
//может быть краш => испольщуем Looper)
//runOnUiThread использует под копотом Handler
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //главный поток
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            println("HANDLE_MSG $msg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
            loadData()
        }
        handler.sendMessage(Message.obtain(handler, 0, 1))
    }

    private fun loadData() {
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        loadCity {
            binding.tvLocation.text = it
            loadTemperature(it) {
                binding.tvTemperature.text = it.toString()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
            }
        }
    }

    private fun loadCity(callback: (String) -> Unit) {
        thread {
            Thread.sleep(5000)
            runOnUiThread { callback.invoke("Moskow") }
        }
    }

    private fun loadTemperature(city: String, callback: (Int) -> Unit) {
        thread {
            // Looper.prepare()
            //главный поток
            //Handler(Looper.getMainLooper())
            runOnUiThread {
                Toast.makeText(
                    this,
                    getString(R.string.loading_temperature_toast, city),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Thread.sleep(5000)
            runOnUiThread {
                callback.invoke(17)
            }
        }
    }
}
