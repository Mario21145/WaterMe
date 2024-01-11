package com.example.waterme.adapater

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.waterme.R
import com.example.waterme.model.Plant
import com.example.waterme.repository.DataStore
import com.example.waterme.viewmodel.PlantViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class PlantAdapter(viewModel: PlantViewModel, private val dataStore: DataStore, private val clickListener: (Plant) -> Unit) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    var stateSwitch by Delegates.notNull<Boolean>()
    private var plantsData: List<Plant> = viewModel.plants.value ?: emptyList()

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var plantImage: ImageView = itemView.findViewById(R.id.PlantImage)
        var plantName: TextView = itemView.findViewById(R.id.name)
        var plantType: TextView = itemView.findViewById(R.id.type)
        var plantDescription: TextView = itemView.findViewById(R.id.description)
        var plantSchedule: TextView = itemView.findViewById(R.id.schedule)
        var customTime: Button = itemView.findViewById(R.id.customTime)
        var switch : Switch = itemView.findViewById(R.id.choiceNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item, parent, false)
        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantsData[position]

        holder.plantName.text = plant.name
        holder.plantType.text = plant.type
        holder.plantDescription.text = plant.description
        holder.plantSchedule.text = plant.schedule

        holder.plantImage.load(plant.image) {
            crossfade(true)
            placeholder(R.drawable.loading)
            error(R.drawable.pictures)
        }

        holder.customTime.setOnClickListener{
            clickListener(plant)
        }

        holder.switch.setOnClickListener {
//            GlobalScope.launch(Dispatchers.IO) {
//                Log.d("Data" , plant.name + " " + holder.switch.isChecked)
//                withContext(Dispatchers.Main){
//                    checkSwitch(holder.switch)
//                }
//                dataStore.saveStateNotification(plant.name , holder.switch.isChecked)
//            }
        }
    }

    override fun getItemCount(): Int {
        return plantsData.size
    }



//    suspend fun checkSwitch(switch: Switch){
//        switch.isActivated = dataStore.statePlantFlow.first()
//    }

    fun setPlantsData(plants: List<Plant>) {
        plantsData = plants
        notifyDataSetChanged()
    }



}
