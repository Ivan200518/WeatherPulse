package com.example.weatherpulse

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherpulse.data.WeatherModel
import com.example.weatherpulse.screens.DialogSearch
import com.example.weatherpulse.screens.MainCard
import com.example.weatherpulse.screens.TableLayout

import com.example.weatherpulse.ui.theme.WeatherPulseTheme
import org.json.JSONObject

const val API_KEY = "f73d1578362f4a8d84a195050243112"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPulseTheme {
                val daysList =  remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember{
                    mutableStateOf(false)
                }

                val currentDay = remember{
                    mutableStateOf(WeatherModel("","","0.0","",
                        "","0.0","0.0",""))
                }
                if (dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        getData(it,this, daysList,currentDay)
                    })
                }
                Image(
                    painter = painterResource(id = R.drawable.sky),
                    contentDescription = "im1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.7f), contentScale = ContentScale.FillBounds
                )
                Column (){
                    MainCard(currentDay, onClickSync = {
                        getData("Samara",this@MainActivity, daysList,currentDay)
                    }, onCLickSearch = {
                        dialogState.value = true
                    })
                    TableLayout(daysList,currentDay)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherPulseTheme {
        Greeting("Android")
    }
}
private fun getData(city: String, context: Context, daysList : MutableState<List<WeatherModel>>,
currentDay: MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY&q=$city&days=3&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(Request.Method.GET,url,{
        response ->
        val list = getWeatherByDays(response)
        currentDay.value = list[0]
        daysList.value = list

    }, {
        error ->
        Log.d("MyLog", "Error: $error")
    })


    queue.add(stringRequest)
}


private fun getWeatherByDays(response: String): List<WeatherModel>{
    if (response.isEmpty()) return  listOf()
    val listOfWeatherModel : ArrayList<WeatherModel> = arrayListOf()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        val time = item.getString("date")
        val icon = item.getJSONObject("day").getJSONObject("condition").getString("icon")
        val condition = item.getJSONObject("day").getJSONObject("condition").getString("text")
        val maxTemp = item.getJSONObject("day").getString("maxtemp_c")
        val minTemp = item.getJSONObject("day").getString("mintemp_c")
        val hours = item.getJSONArray("hour").toString()
        val weatherModel = WeatherModel(city,time,"",condition,icon,maxTemp,minTemp,hours)
        listOfWeatherModel.add(weatherModel)
    }
    listOfWeatherModel[0] = listOfWeatherModel[0].copy(time = mainObject.getJSONObject("current").getString("last_updated"), tempCurrent = mainObject.getJSONObject("current").getString("temp_c"))

    return listOfWeatherModel




}