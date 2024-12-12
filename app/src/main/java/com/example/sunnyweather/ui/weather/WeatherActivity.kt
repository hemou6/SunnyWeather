package com.example.sunnyweather.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    lateinit var binding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng=intent.getStringExtra("location_lng")?:""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat=intent.getStringExtra("location_lat")?:""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName=intent.getStringExtra("place_name")?:""
        }
        viewModel.weatherLiveData.observe(this, Observer{result->
            val weather=result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
    }

    private fun showWeatherInfo(weather:Weather){
        val realtime=weather.realtime
        val daily=weather.daily
        binding.now.placeName.text=viewModel.placeName
        binding.now.currentTemp.text="${realtime.temperature.toInt()}"
        binding.now.currentSky.text= getSky(realtime.skycon).info
        binding.now.currentAQI.text="空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        binding.forecast.forecastLineLayout.removeAllViews()
        val days=daily.skycon.size
        for(i in 0 until days){
            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]
            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,binding.forecast.forecastLineLayout,false)
            val dateInfo=view.findViewById(R.id.dateInfo)as TextView
            val skyIcon=view.findViewById(R.id.skyIcon)as ImageView
            val skyInfo=view.findViewById(R.id.skyInfo)as TextView
            val temperatureInfo=view.findViewById(R.id.temperatureInfo)as TextView
            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky= getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText="${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text=tempText
            binding.forecast.forecastLineLayout.addView(view)
        }
        val lifeIndex=daily.lifeIndex
        binding.lifeIndex.coldRiskText.text=lifeIndex.coldRisk[0].desc
        binding.lifeIndex.dressingText.text=lifeIndex.dressing[0].desc
        binding.lifeIndex.ultravioletText.text=lifeIndex.ultraviolet[0].desc
        binding.lifeIndex.carWashingText.text=lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility=View.VISIBLE
    }
}