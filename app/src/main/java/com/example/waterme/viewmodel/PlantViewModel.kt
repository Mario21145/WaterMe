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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.data.DataSource
import com.example.waterme.model.Plant
import com.example.waterme.network.PlantApi
import com.example.waterme.worker.WaterReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlantViewModel(application: Application): ViewModel() {

enum class PlantsApiStatus{DONE , ERROR , LOADING , DEFAULT}

    private var _status = MutableLiveData<PlantsApiStatus>().apply {
        value = PlantsApiStatus.DEFAULT
    }
    val status: LiveData<PlantsApiStatus> = _status

    private val _plants = MutableLiveData<List<Plant>>().apply {
        listOf<Plant>()
    }
    var plants : MutableLiveData<List<Plant>> = _plants

    fun fetchPlants() {
       viewModelScope.launch {
           while (_status.value != PlantsApiStatus.DONE) {
               try {
                   _plants.value = PlantApi.retrofitService.getPlantList().plants
                   _status.value = PlantsApiStatus.DONE
               } catch (e: Exception) {
                   _status.value = PlantsApiStatus.ERROR
                   _plants.value = listOf()
               }
           }
       }
    }

    init {
        fetchPlants()
    }

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plantName: String
    ) {

        val inputData = Data.Builder().putString(WaterReminderWorker.nameKey, plantName).build()

        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(duration, unit)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance().enqueueUniqueWork(
            "waterReminderWork",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

    }
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
