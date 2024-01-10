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
package com.example.waterme

import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.waterme.adapater.PlantAdapter
import com.example.waterme.repository.DataStore
import com.example.waterme.repository.dataStore
import com.example.waterme.ui.ReminderDialogFragment
import com.example.waterme.viewmodel.PlantViewModel
import com.example.waterme.viewmodel.PlantViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lifecycleScope.launch{
            Log.d("Data" , viewModel.dataStore.returnData())
        }


        val adapter = PlantAdapter(viewModel , viewModel.dataStore) { plant ->
            runOnUiThread {
                val dialog = ReminderDialogFragment(plant.name)
                dialog.show(supportFragmentManager, "WaterReminderDialogFragment")
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter

        viewModel.plants.observe(this) { plants ->
            adapter.setPlantsData(plants)
        }



    }
}
