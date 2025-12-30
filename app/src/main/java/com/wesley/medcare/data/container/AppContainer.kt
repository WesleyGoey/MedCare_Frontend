package com.wesley.medcare.data.container

import android.content.Context
import com.google.gson.GsonBuilder
import com.wesley.medcare.data.repository.HistoryRepository
import com.wesley.medcare.data.repository.MedicineRepository
import com.wesley.medcare.data.repository.UserRepository
import com.wesley.medcare.data.service.HistoryService
import com.wesley.medcare.data.service.MedicineService
import com.wesley.medcare.data.service.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {
    companion object {

//        const val ROOT_URL = "http://10.222.192.93:3000" //hotspot wesley
        const val ROOT_URL = "http://192.168.1.5:3000" //wifi wesley
//        const val ROOT_URL = "http://10.0.188.86:3000" //staff uc
//        const val ROOT_URL = "http://192.168.1.72:3000" //mrn
        const val BASE_URL = "${ROOT_URL}/api/"
    }


    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .baseUrl(BASE_URL)
        .build()

    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(context, userService)
    }

    private val medicineService: MedicineService by lazy {
        retrofit.create(MedicineService::class.java)
    }

    val medicineRepository: MedicineRepository by lazy {
        MedicineRepository(medicineService, context)
    }

    private val historyService: HistoryService by lazy {
        retrofit.create(HistoryService::class.java)
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepository(historyService, context)
    }
}
