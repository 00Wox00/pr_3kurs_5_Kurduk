package com.example.weatherapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class WeatherMain(val temp: Double, val pressure: Int)
class WeatherWind(val speed: Double)
class WeatherResponse(val name: String, val main: WeatherMain, val wind: WeatherWind)

class MainActivity : AppCompatActivity() {

    val key: String = "aab0109785b7c6697cfbc0372a3b07ad"
    lateinit var etCity: EditText
    lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity = findViewById(R.id.etCity)
        tvResult = findViewById(R.id.tvResult)
        val btnGet: Button = findViewById(R.id.btnGet)

        btnGet.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                getWeather()
            }
        })
    }

    fun getWeather() {
        val city: String = etCity.text.toString().trim()
        if (city.isEmpty()) {
            etCity.error = "Введите город"
            return
        }

        val url: String = "https://api.openweathermap.org/data/2.5/weather?q=" +
                Uri.encode(city) + "&appid=" + key + "&units=metric&lang=ru"

        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url,
            object : Response.Listener<String> {
                override fun onResponse(response: String) {
                    showWeather(response)
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.d("WEATHER", "Ошибка: " + error.toString())
                    tvResult.text = "Ошибка: город не найден или нет сети"
                }
            })

        queue.add(request)
    }

    fun showWeather(json: String) {
        val weather: WeatherResponse = Gson().fromJson(json, WeatherResponse::class.java)

        Log.d("WEATHER", "Город: " + weather.name + ", температура: " + weather.main.temp)

        val mm: Int = (weather.main.pressure * 0.75).toInt()
        tvResult.text = "Город: " + weather.name + "\n" +
                "Температура: " + weather.main.temp + " °C\n" +
                "Давление: " + mm + " мм рт. ст.\n" +
                "Скорость ветра: " + weather.wind.speed + " м/с"
    }
}