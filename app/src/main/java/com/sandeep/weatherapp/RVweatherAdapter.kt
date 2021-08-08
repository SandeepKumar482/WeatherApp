package com.sandeep.weatherapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat

class RVweatherAdapter(val context: Context, val WeatherList: ArrayList<RVweather>) : RecyclerView.Adapter<RVweatherAdapter.RVweatherViewHolder>() {


    class RVweatherViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtTime:TextView=view.findViewById(R.id.txtTime)
        val RvTemp:TextView=view.findViewById(R.id.rvTxtTemp)
        val ivCondition:ImageView=view.findViewById(R.id.ivRvCondition)
        val txtWindSpeed:TextView=view.findViewById(R.id.txtWindSpeed)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVweatherViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.rv_item,parent,false)
        return RVweatherViewHolder(view)

    }

    override fun onBindViewHolder(holder: RVweatherViewHolder, position: Int) {
        var weather=WeatherList[position]
        try{
            val formater=SimpleDateFormat("yyyy-hh-dd hh:mm")
            val date=formater.parse(weather.time)
            holder.txtTime.text=date.toString()
        }catch(e:ParseException){
            println("$.it")
        }
        //holder.txtTime.text=weather.time
        holder.RvTemp.text=weather.temp+"°C"
        Picasso.get().load("https:"+weather.icon).into(holder.ivCondition)

        holder.txtWindSpeed.text=weather.wind+"Km/h"
    }

    override fun getItemCount(): Int {
        return WeatherList.size
    }

}