package com.example.weatherforecasting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context cintext;
    private ArrayList<RvModel>RvModelArrayList;

    public WeatherRVAdapter(Context cintext, ArrayList<RvModel> rvModelArrayList) {
        this.cintext = cintext;
        RvModelArrayList = RvModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cintext).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
    RvModel model = RvModelArrayList.get(position);
        holder.Temperature.setText(model.getTemperature()+"°C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.IconWeather);
        holder.WindSpeed.setText(model.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd, hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm, aa");
        try{
            Date t = input.parse(model.getTime());
            holder.Time.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();


        }

    }

    @Override
    public int getItemCount() {
        return RvModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Time, Temperature, WindSpeed;
        ImageView IconWeather;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Time = itemView.findViewById(R.id.idTVTime);
            Temperature = itemView.findViewById(R.id.idTVTemparature);
            WindSpeed = itemView.findViewById(R.id.TVwindSpeed);
            IconWeather = itemView.findViewById(R.id.tvCondition);
        }
    }
}
