package com.example.waterme.database.plant

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.waterme.model.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

//    @Insert(onConflict = REPLACE)
//    fun insertAll(plants: List<Plant>)
/*
    @Query("INSERT INTO plantdb (name, schedule, type, description, image, state) VALUES (:name, :schedule, :type, :description, :image, :state)")
    fun insertPlant(name: String, schedule: String, type: String, description: String, image: String, state: Boolean)
*/
    @Query("SELECT * FROM plantdb")
    fun getAllPlants(): List<Plant>

//    @Query("SELECT * FROM schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC")
//    fun getByStopName(stopName: String): Flow<List<Schedule>>

//    @Query("SELECT * FROM schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC")
//    fun getByStopName(stopName: String): Flow<List<Schedule>>


}