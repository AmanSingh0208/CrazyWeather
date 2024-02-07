package com.example.crazyweather

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.crazyweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//API Key - 36beae415b282a76e5a728bbf036cd26
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Delhi")
        getCity()
    }

    private fun getCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "36beae415b282a76e5a728bbf036cd26", "metric")
        response.enqueue(object : Callback<CrazyWeather> {
            override fun onResponse(call: Call<CrazyWeather>, response: Response<CrazyWeather>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString().substringBefore(".")
                    val humidity = responseBody.main.humidity.toString()
                    val maxTemp = responseBody.main.temp_max.toString().substringBefore(".")
                    val minTemp = responseBody.main.temp_min.toString().substringBefore(".")
                    val condition = responseBody.weather.firstOrNull()?.main ?: "Unknown "
                    val sea = responseBody.main.sea_level.toString()
                    val wind = responseBody.wind.speed.toString()
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()

                    binding.temprature.text = "$temperature"
                    binding.humidity.text = "$humidity%"
                    binding.minTemp.text = "MIN: $minTemp°C"
                    binding.maxTemp.text = "MAX: $maxTemp°C"
                    binding.conditions.text = "$condition"
                    binding.weather.text = "$condition"
                    binding.seaLevel.text = "$sea hPa"
                    binding.wind.text = "$wind Km/s"
                    binding.sunrise.text = "${getTime(sunrise)}"
                    binding.sunset.text = "${getTime(sunset)}"
                    binding.date.text = getDate()
                    binding.day.text = getDay(System.currentTimeMillis())
                    binding.cityName.text = "${cityName.toUpperCase()}"

                    Log.e("TAG", "On response $temperature")
                    changeIconandBackground(condition)
                }
            }

            override fun onFailure(call: Call<CrazyWeather>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeIconandBackground(conditions: String) {
        when (conditions) {
            "Clouds", "Partially Clouds", "Overcast", "Mist", "Foggy", "Haze", "Patch Fog", "Dust Storm", "Sandstorm" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Shower", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Freezing Rain", "Sleet", "Ice Pellets", "Light Hail", "Heavy Hail", "Frost", "Black Ice" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }


    private fun getDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun getTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    private fun getDay(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}