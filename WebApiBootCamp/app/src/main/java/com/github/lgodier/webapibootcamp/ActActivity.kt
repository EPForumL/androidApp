package com.github.lgodier.webapibootcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import com.github.lgodier.webapibootcamp.cache.Activity
import com.github.lgodier.webapibootcamp.cache.ActivityDAO
import com.github.lgodier.webapibootcamp.cache.AppDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.Random

class ActActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()

        val activityDao = db.activityDAO()
        try{
            askForActivity(activityDao)
        } catch (e: Exception) {}

    }

    //open fun getBaseUrl() = "https://www.boredapi.com/api/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.boredapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun askForActivity(activityDao: ActivityDAO) {

        val boredApi = retrofit.create(BoredApi::class.java)

        boredApi.getActivity().enqueue(object : Callback<BoredActivity> {
            override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {
                if (response.isSuccessful) {
                    var activity = response.body()
                    findViewById<TextView>(R.id.textView).text = "ACTIVITY: " + activity!!.activity
                    activityDao.insertAll(Activity(activityDao.count()+1, activity.activity))
                }
            }

            override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                val random = Random()

                findViewById<TextView>(R.id.textView).text = activityDao.loadById(
                    random.nextInt(activityDao.count())).description
            }
        })
    }
    data class BoredActivity(
        val activity: String,

        )

    interface BoredApi {
        @GET("activity")
        fun getActivity(): Call<BoredActivity>
    }
}