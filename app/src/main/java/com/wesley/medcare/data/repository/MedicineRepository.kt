package com.wesley.medcare.data.repository

import com.wesley.medcare.data.dto.Medicine.AddMedicineRequest
import com.wesley.medcare.data.dto.Medicine.GetAllMedicinesResponse
import com.wesley.medcare.data.dto.Medicine.GetLowStockResponse
import com.wesley.medcare.data.dto.Medicine.GetMedicineByIdResponse
import com.wesley.medcare.data.dto.Medicine.UpdateMedicineRequest
import com.wesley.medcare.data.service.MedicineService

class MedicineRepository(private  val service: MedicineService) {
    suspend fun getAllMedicines(): GetAllMedicinesResponse {
        return service.getAllMedicines().body()!!
    }

    suspend fun getLowStock(threshold: Int? = null): GetLowStockResponse {
        return service.checkLowStock(threshold).body()!!
    }

    suspend fun getMedicineById(id: Int): GetMedicineByIdResponse {
        return service.getMedicineById(id).body()!!
    }

    suspend fun addMedicine(request: AddMedicineRequest): GetMedicineByIdResponse {
        return service.addMedicine(request).body()!!
    }

    suspend fun updateMedicine(id: Int, request: UpdateMedicineRequest): GetMedicineByIdResponse {
        return service.updateMedicine(id, request).body()!!
    }

    suspend fun deleteMedicine(id: Int): Boolean {
        val resp = service.deleteMedicine(id)
        return resp.isSuccessful
    }
}


//class WeatherRepository (private val service: WeatherService) {
//    suspend fun getWeatherByCity(cityName:String): PanPanWeather{
//        val weather = service.getWeather(
//            city = cityName,
//            apiKey = "c98f91bafdefa4e1a57e2598501305e0",
//            units = "metric"
//        ).body()!!
//
//        return PanPanWeather(
//            city = weather.name,
//            dateTime = weather.dt,
//            icon = weather.weather[0].icon,
//            condition = weather.weather[0].main,
//            temperature = weather.main.temp,
//            humidity = weather.main.humidity,
//            wind = weather.wind.speed,
//            feelsLike = weather.main.feels_like,
//            rainFall = weather.rain?.`1h` ?: 0.0,
//            pressure = weather.main.pressure,
//            clouds = weather.clouds.all,
//            sunrise = weather.sys.sunrise,
//            sunset = weather.sys.sunset,
//            isError = false,
//            errorMessage = null
//        )
//    }
//}