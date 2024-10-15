package com.example.weatherforecastingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import android.widget.TextView
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var resultCard: CardView
    private lateinit var cityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        resultCard = findViewById(R.id.resultCard)
        cityTextView = findViewById(R.id.cityTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        humidityTextView = findViewById(R.id.humidityTextView)
        windSpeedTextView = findViewById(R.id.windSpeedTextView)

        resultCard.visibility = View.GONE

        searchButton.setOnClickListener {
            val location = searchEditText.text.toString()
            if (location.isNotEmpty()) {
                fetchWeatherData(location)
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherData(location: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$location&appid=e32c5795de4f06df08ca1cd0423314ce&units=metric").readText()
                }
                updateUI(response)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(response: String) {
        val jsonObj = JSONObject(response)
        val main = jsonObj.getJSONObject("main")
        val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
        val wind = jsonObj.getJSONObject("wind")

        val city = jsonObj.getString("name")
        val temp = main.getDouble("temp")
        val description = weather.getString("description").capitalize()
        val humidity = main.getInt("humidity")
        val windSpeed = wind.getDouble("speed")

        cityTextView.text = city
        temperatureTextView.text = "${temp.toInt()}°C"
        descriptionTextView.text = description
        humidityTextView.text = "Humidity: $humidity%"
        windSpeedTextView.text = "Wind: $windSpeed m/s"

        resultCard.visibility = View.VISIBLE
    }
}