/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.waterme.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.model.Plant
import com.example.waterme.network.PlantApi
import com.example.waterme.repository.DataStore
import com.example.waterme.worker.WaterReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.example.waterme.repository.dataStore

class PlantViewModel(application: Application): ViewModel() {

enum class PlantsApiStatus{DONE , ERROR , DEFAULT}

    private var _status = MutableLiveData<PlantsApiStatus>().apply {
        value = PlantsApiStatus.DEFAULT
    }
    val status: LiveData<PlantsApiStatus> = _status

    private val _plants = MutableLiveData<List<Plant>>()
    var plants : MutableLiveData<List<Plant>> = _plants

    val dataStore = DataStore(application.dataStore)

    init {
        fetchPlants()
    }

    fun fetchPlants() {
       viewModelScope.launch {
           withContext(Dispatchers.IO) {
               while (_status.value != PlantsApiStatus.DONE) {
                   try {
                       _plants.postValue(PlantApi.retrofitService.getPlantList().plants)
                       if(_plants.value!!.isNotEmpty()) {
                           _status.postValue(PlantsApiStatus.DONE)
                       }
                   } catch (e: Exception) {
                       _status.postValue(PlantsApiStatus.ERROR)
                       _plants.postValue(listOf())
                   }
               }
           }
       }
    }

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plantName: String
    ) {

        val inputData = Data.Builder().putString(WaterReminderWorker.nameKey, plantName).build()

        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            repeatInterval = duration,
            repeatIntervalTimeUnit = unit,
        )
            .setInitialDelay(duration,unit)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "uniqueWorkName",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )


    }


//    fun setDataInDataStore(plantName : String , switch : Boolean){
//        viewModelScope.launch {
//            dataStore.saveStateNotification(plantName , switch)
//            Log.d("Data", dataStore.returnData())
//        }
//    }

}

class PlantViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            PlantViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
