
package com.sandeep.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var rlHome: RelativeLayout
    private lateinit var txtCityName: TextView
    private lateinit var imgBackground: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var etCityName: TextInputEditText
    private lateinit var imgSearch: ImageView
    private lateinit var txtTemp: TextView
    private lateinit var ivTemp: ImageView
    private lateinit var txtCondition: TextView
    private lateinit var weatherAdapter: RVweatherAdapter
    private lateinit var locationManager: LocationManager
    private var permissionCode: Int = 1
    private lateinit var cityName: String
    private val weatherList = arrayListOf<RVweather>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        imgBackground = findViewById(R.id.imgBackgroung)
        progressBar = findViewById(R.id.PBloading)
        rlHome = findViewById(R.id.RLhome)
        txtCityName = findViewById(R.id.txtCityName)
        recyclerView = findViewById(R.id.recView)
        etCityName = findViewById(R.id.etCityName)
        imgSearch = findViewById(R.id.ivSearchImage)
        txtTemp = findViewById(R.id.txtTemp)
        ivTemp = findViewById(R.id.ivTemp)
        txtCondition = findViewById(R.id.txtCondition)

        weatherAdapter = RVweatherAdapter(this, weatherList)
        recyclerView.adapter = weatherAdapter

        cityName ="delhi"

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

                    fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                        var location: Location? = task.result
                        // println("$.it")
                        if (location != null) {
                            //   println("$.it")
                            cityName = getCityName(location.longitude, location.latitude)
                            getWeatherInfo(cityName)
                        }
                    }
                } else {
                    progressBar.visibility = View.GONE
                    rlHome.visibility = View.VISIBLE
                    weatherList.clear()
                }
            }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION)

        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            var location: Location? = task.result
            // println("$.it")
            if (location != null) {
                //   println("$.it")
                cityName = getCityName(location.longitude, location.latitude)
                getWeatherInfo(cityName)
            }
        }



        imgSearch.setOnClickListener {
            val city: String = etCityName.text.toString()
            txtCityName.text = city
            getWeatherInfo(city)

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please Grant the Permission ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeatherInfo(cityName: String) {
        txtCityName.text = cityName

        var cityName = cityName.replace(" ","")
        val url: String =
            "http://api.weatherapi.com/v1/forecast.json?key=adbcd73789bd473a9a5112234210508&q=$cityName&days=10&aqi=yes&alerts=yes"

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url, null, com.android.volley.Response.Listener {
                print("Response Is $it")
                progressBar.visibility = View.GONE
                rlHome.visibility = View.VISIBLE
                weatherList.clear()
                print("Respionse Is : $it")
                try {
                    val temperature: String = it.getJSONObject("current").getString("temp_c")
                    txtTemp.text = temperature+"°C "
                    val isday: Int = it.getJSONObject("current").getInt("is_day")
                    val condition: String =
                        it.getJSONObject("current").getJSONObject("condition").getString("text")
                    txtCondition.text = condition
                    val conditionIcon: String =
                        it.getJSONObject("current").getJSONObject("condition").getString("icon")
                    Picasso.get().load("https:$conditionIcon").into(ivTemp)
                    if (isday == 1) {
                        //morning time
                        Picasso.get().load("https://cdn.wallpapersafari.com/76/14/T6ndPX.jpg")
                            .into(imgBackground)
                    } else {
                        //night time
                        Picasso.get()
                            .load("https://images.unsplash.com/photo-1472552944129-b035e9ea3744?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxleHBsb3JlLWZlZWR8M3x8fGVufDB8fHx8&w=1000&q=80")
                            .into((imgBackground))
                    }
                    val forecastObject: JSONObject = it.getJSONObject("forecast")
                    val forecast: JSONObject =
                        forecastObject.getJSONArray("forecastday").getJSONObject(0)
                    val hour: JSONArray = forecast.getJSONArray("hour")

                    for (i in 0 until hour.length()) {
                        val hourObj: JSONObject = hour.getJSONObject(i)
                        val time: String = hourObj.getString("time")
                        val temp: String = hourObj.getString("temp_c")
                        val wind: String = hourObj.getString("wind_kph")
                        val icon: String = hourObj.getJSONObject("condition").getString("icon")
                        val weatherObject = RVweather(time, temp, wind, icon)
                        weatherList.add(weatherObject)
                    }
                    weatherAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    println(" Exception IS $e")
                    Toast.makeText(this, "Enter a Valid City Name", Toast.LENGTH_SHORT).show()
                }

            },
                com.android.volley.Response.ErrorListener {
                    print("Erorr Is $it")
                    Toast.makeText(this, "Connection Error Occurred", Toast.LENGTH_SHORT).show()
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "adbcd73789bd473a9a5112234210508"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)


    }

    private fun getCityName(lon: Double, lat: Double): String {
        var cityName = "delhi"
        val geo = Geocoder(baseContext, Locale.getDefault())
        try {
            val address: List<Address> = geo.getFromLocation(lat, lon, 10)
            for (adr in address) {
                val city: String = adr.locality
                if (city != " ") {
                    cityName = city
                } else {
                    Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show()

                }


            }
        } catch (e: Exception) {
            println("$.it")
        }
        return cityName
    }
}