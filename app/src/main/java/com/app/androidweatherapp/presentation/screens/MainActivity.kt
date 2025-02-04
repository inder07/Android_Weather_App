package com.app.androidweatherapp.presentation.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.androidweatherapp.R
import com.app.androidweatherapp.data.remote.Constants
import com.app.androidweatherapp.data.remote.Constants.IMPERIAL
import com.app.androidweatherapp.data.remote.Constants.METRIC
import com.app.androidweatherapp.databinding.ActivityMainBinding
import com.app.androidweatherapp.domain.model.WeatherListDomain
import com.app.androidweatherapp.domain.model.WeatherLocationResponseDomain
import com.app.androidweatherapp.presentation.adapter.WeatherForecastAdapter
import com.app.androidweatherapp.presentation.viewmodel.WeatherForecastViewmodel
import com.app.androidweatherapp.utils.extension.GpsTracker
import com.app.androidweatherapp.utils.extension.getDateTime
import com.app.androidweatherapp.utils.extension.hideKeyboard
import com.app.androidweatherapp.utils.extension.logTrace
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewmodel: WeatherForecastViewmodel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherForecastAdapter: WeatherForecastAdapter
    private var gpsTracker: GpsTracker? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    private var city: String? = ""
    private var country: String? = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRefreshLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerForecast.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weatherForecastAdapter = WeatherForecastAdapter(this,
            object : WeatherForecastAdapter.OnWeatherListListener {
                override fun onWeatherListener(weatherListData: WeatherListDomain) {
                    launchWeatherDetailsScreen(weatherListData, binding.tempSwitch.isChecked)
                }
            })
        binding.recyclerForecast.adapter = weatherForecastAdapter

        getUserLocation()
        initView()

        binding.mainRefreshLayout.setColorSchemeColors(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW
        )
        binding.mainRefreshLayout.setOnRefreshListener {
            getUserLocation()
            initView()
        }

        updateTextColors(binding.tempSwitch.isChecked, binding.tvCelsius, binding.tvFahrenheit)

        binding.tempSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateTextColors(isChecked, binding.tvCelsius, binding.tvFahrenheit)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTextColors(isChecked: Boolean, tvCelsius: TextView, tvFahrenheit: TextView) {
        if (isChecked) {
            tvCelsius.setTextColor(Color.GRAY)
            tvFahrenheit.setTextColor(Color.GREEN)
            initView()
        } else {
            tvCelsius.setTextColor(Color.RED)
            tvFahrenheit.setTextColor(Color.GRAY)
            initView()
        }
    }

    private fun getUserLocation() {
        try {
            (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        gpsTracker = GpsTracker(this)
        if (gpsTracker?.canGetLocation() == true) {
            latitude = gpsTracker?.userLatitude
            longitude = gpsTracker?.userLongitude
        } else {
            gpsTracker?.showSettingsAlert()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.isLoadingFlow.collect { isLoading ->
                    showLoading(isLoading)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.fetchWeather(
                    this@MainActivity,
                    latitude = latitude ?: 0.0,
                    longtitude = longitude ?: 0.0,
                    units = if (binding.tempSwitch.isChecked) IMPERIAL else METRIC
                )
                viewmodel.weatherData.collectLatest { response ->
                    response?.list.let { weatherList ->
                        weatherForecastAdapter.setTransaction(
                            weatherList,
                            binding.tempSwitch.isChecked
                        )
                    }

                    setCurrentWeather(response)
                    logTrace("WeatherForecast $response")
                }
            }
        }

        binding.searchBarEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(cityName: Editable?) {
                try {
                    searchFilter(cityName.toString())
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchFilter(cityName: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.isLoadingFlow.collect { isLoading ->
                    showLoading(isLoading)
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.fetchWeatherByCity(
                cityName,
                if (binding.tempSwitch.isChecked) IMPERIAL else METRIC
            )
            viewmodel.weatherData.collectLatest { cityResponse ->
                cityResponse?.list.let { cityWeatherList ->
                    weatherForecastAdapter.setTransaction(
                        cityWeatherList,
                        binding.tempSwitch.isChecked
                    )
                }
                setCurrentWeather(cityResponse)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setCurrentWeather(response: WeatherLocationResponseDomain?) {
        binding.mainRefreshLayout.isRefreshing = false
        var position = 0
        val iconName = response?.list?.get(position)?.weather?.get(0)?.icon

        if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "MONDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.monday_color
                )
            )
        } else if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "TUESDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.tuesday_color
                )
            )
        } else if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "WEDNESDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.wednesday_color
                )
            )
        } else if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "THURSDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.thursday_color
                )
            )
        } else if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "FRIDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.friday_color
                )
            )
        } else if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "SATURDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.mainTextColor
                )
            )
        } else if (response?.list?.get(position)?.dt?.let {
                getDateTime(it)!!.name == "SUNDAY"
            } == true) {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.sunday_color
                )
            )
        } else {
            binding.containerForecastBgd.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        }

        city = response?.city?.name
        country = response?.city?.country
        binding.cityName.text = "$city, $country"
        Picasso.get().load("https://openweathermap.org/img/wn/$iconName@2x.png")
            .placeholder(this.resources.getDrawable(R.drawable.a10d_svg))
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(binding.weatherIcon)
        binding.textViewTemperature.text =
            response?.list?.get(position)?.main?.temp.toString()
                .substringBefore(".") + if (binding.tempSwitch.isChecked) "°F" else "°C"
        binding.textViewWeatherMain.text =
            response?.list?.get(position)?.weather?.get(position)?.main
        binding.textViewHumidity.text =
            response?.list?.get(position)?.main?.humidity.toString() + "%"
    }

    private fun launchWeatherDetailsScreen(weatherListData: WeatherListDomain, units: Boolean) {
        val intent = Intent(this, WeatherForecastDetails::class.java).apply {
            putExtra(Constants.WEATHER_DETAILS, weatherListData)
            putExtra(Constants.CITY, city)
            putExtra(Constants.COUNTRY, country)
            putExtra(Constants.UNITS, if (units) "Fahrenheit" else "Celsius" )
        }
        startActivity(intent)

    }

    private fun showLoading(isEnabled: Boolean) {
        binding.progressBar.isVisible = isEnabled
    }
}