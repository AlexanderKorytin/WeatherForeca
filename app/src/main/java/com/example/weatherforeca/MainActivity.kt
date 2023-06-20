package com.example.weatherforeca

import ForecaAuthRequest
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val currentWeatherBaseUrl = "https://pfa.foreca.com"
    private var token =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9wZmEuZm9yZWNhLmNvbVwvYXV0aG9yaXplXC90b2tlbiIsImlhdCI6MTY4NzI4NTk5MSwiZXhwIjo5OTk5OTk5OTk5LCJuYmYiOjE2ODcyODU5OTEsImp0aSI6IjlkY2YxZGRjNDEwODI4NGUiLCJzdWIiOiJhbC1jb3J5dGluIiwiZm10IjoiWERjT2hqQzQwK0FMamxZVHRqYk9pQT09In0.dSeJSS3eSYKkK9jtFohxU-YIzcfLy9PiJzINKudWeXA"
    private val retrofit = Retrofit.Builder()
        .baseUrl(currentWeatherBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val forecaService = retrofit.create(ForecaApi::class.java)
    private val locations = ArrayList<ForecastLocation>()
    private val adapter = LocationsAdapter { showWeather(it) }
    private lateinit var searchButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var locationsList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        searchButton = findViewById(R.id.searchButton)
        queryInput = findViewById(R.id.queryInput)
        locationsList = findViewById(R.id.locations)
        adapter.locations = locations

        locationsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        locationsList.adapter = adapter
        searchButton.setOnClickListener {
            if (queryInput.text.isNotEmpty()) {
                if (token.isEmpty()) {
                    authenticate()
                } else {
                    search()
                }
            }
        }

        searchButton.setOnClickListener {
            if (queryInput.text.isNotEmpty()) {
                if (token.isEmpty()) {
                    authenticate()
                } else {
                    search()
                    val imput = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imput.hideSoftInputFromWindow(queryInput.windowToken, 0)
                }
            }
        }


    }

    private fun authenticate() {
        forecaService.authenticate(ForecaAuthRequest("al-corytin", "EgVysb6WJ1Q3"))
            .enqueue(object : Callback<ForecaAuthResponse> {
                override fun onResponse(
                    call: Call<ForecaAuthResponse>,
                    response: Response<ForecaAuthResponse>
                ) {
                    if (response.code() == 200) {
                        token = response.body()?.token.toString()
                        search()
                    } else {
                        showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<ForecaAuthResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }

            })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            locations.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun search() {
        forecaService.getLocations("Bearer $token", queryInput.text.toString())
            .enqueue(object : Callback<LocationsResponse> {
                override fun onResponse(
                    call: Call<LocationsResponse>,
                    response: Response<LocationsResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.locations?.isNotEmpty() == true) {
                                locations.clear()
                                locations.addAll(response.body()?.locations!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", "")
                            } else {
                                showMessage(getString(R.string.nothing_found), "")
                            }
                        }

                        401 -> authenticate()
                        else -> showMessage(
                            getString(R.string.nothing_found),
                            response.code().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }

            })
    }

    private fun showWeather(location: ForecastLocation) {
        forecaService.getCurrentWeather("Bearer $token", location.id)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {
                    if (response.body()?.current != null) {
                        val message =
                            "${location.name} t: ${response.body()?.current?.temperature}\n" +
                                    "(Ощущается как ${response.body()?.current?.feelsLikeTemp})\n" +
                                    "скорость ветра: ${response.body()?.current?.windSpeed} м/с\n" +
                                    "видимость: ${response.body()?.current?.visibility} м"

                        showMessage(message,"")
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }
}