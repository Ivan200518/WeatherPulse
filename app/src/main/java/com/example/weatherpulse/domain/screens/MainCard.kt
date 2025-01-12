package com.example.weatherpulse.domain.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.weatherpulse.R
import com.example.weatherpulse.domain.screens.models.WeatherModel
import com.example.weatherpulse.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(
    currentDay: MutableState<WeatherModel>,
    onClickSync: () -> Unit,
    onCLickSearch: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueLight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )

                    AsyncImage(
                        model = "https:" + currentDay.value.imageCondition,
                        contentDescription = "im2",
                        modifier = Modifier.size(35.dp)
                    )
                }
                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 25.sp),
                    color = Color.White
                )
                Text(
                    text = if (currentDay.value.tempCurrent.isNotEmpty())
                        currentDay.value.tempCurrent.toFloat().toInt().toString() + "°C"
                    else currentDay.value.maxTemp.toFloat().toInt().toString() +
                            "°C/${currentDay.value.minTemp.toFloat().toInt()}",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White

                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 20.sp),
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        onCLickSearch.invoke()
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "im3",
                            tint = Color.White

                        )
                    }
                    Text(
                        text = "${
                            currentDay.value.maxTemp.toFloat().toInt()
                        }°C/${currentDay.value.minTemp.toFloat().toInt()}",
                        style = TextStyle(fontSize = 20.sp),
                        color = Color.White,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    IconButton(onClick = {
                        onClickSync.invoke()
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "im4",
                            tint = Color.White
                        )

                    }
                }


            }


        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TableLayout(
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = androidx.compose.foundation.pager.rememberPagerState { 2 }
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            contentColor = Color.White
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = text) }, modifier = Modifier.background(BlueLight)
                )

            }
        }
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when (index) {
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, currentDay)

        }

    }


}

private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list: ArrayList<WeatherModel> = arrayListOf()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        val tempCurrent = item.getString("temp_c").toFloat().toInt().toString() + "°C"
        val time = item.getString("time")
        val condition = item.getJSONObject("condition").getString("text")
        val imageCondition = item.getJSONObject("condition").getString("icon")
        val model = WeatherModel("", time, tempCurrent, condition, imageCondition, "", "", "")
        list.add(model)
    }
    return list
}