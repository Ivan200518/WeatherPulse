package com.example.weatherpulse.domain.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.weatherpulse.domain.screens.models.WeatherModel
import com.example.weatherpulse.ui.theme.BlueLight

@Composable
fun MainList(list: List<WeatherModel>, currentDay : MutableState<WeatherModel>, ){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(list) { index, item ->
            ListItem(item,currentDay)
        }
    }
}
@Composable
fun ListItem(item : WeatherModel, currentDay: MutableState<WeatherModel>) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp).clickable {
                if (item.hours.isEmpty()) return@clickable
                    currentDay.value = item
            },
        shape = RoundedCornerShape(5.dp),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueLight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
            ) {
                Text(text = item.time, color = Color.White)
                Text(text = item.condition, color = Color.White)
            }
            Text(text = item.tempCurrent.ifEmpty { item.maxTemp.toFloat().toInt().toString() + "/" + item.minTemp.toFloat().toInt().toString() },
                color = Color.White,
                style = TextStyle(fontSize = 25.sp))
            AsyncImage(
                model = "https:${item.imageCondition}" ,
                contentDescription = "im5",
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 8.dp)
            )
        }


    }

}
@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit : (String) -> Unit){
    val dialogText = remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = {
        dialogState.value = false
    }, confirmButton = {
        TextButton(onClick = {
            onSubmit(dialogText.value)
            dialogState.value = false
            }) {

            Text(text = "OK")
        }
    }, dismissButton = {
        TextButton(onClick = {dialogState.value = false}) {
            Text(text = "Cancel")
        }
    }, title = {
        Column(modifier = Modifier.fillMaxWidth()){
            Text(text = "Enter name of city:")
            TextField(value = dialogText.value, onValueChange = {
                dialogText.value = it
            })
        }

    })
}