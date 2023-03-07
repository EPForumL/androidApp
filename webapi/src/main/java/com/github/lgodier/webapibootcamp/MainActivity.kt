package com.github.lgodier.webapibootcamp

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()

        val activityDao = db.activityDAO()

        val button: Button = findViewById(R.id.GetActivityButton);
        button.setOnClickListener {
            try {
                askForActivity(activityDao)
            } catch (e: Exception) {
                findViewById<TextView>(R.id.textView).text = "Something went wrong :( "     }
        }
    }

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
                    Thread{
                        activityDao.insertAll(Activity(activityDao.count()+1, activity.activity))
                    }.start()
                }
            }

            override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                var message = "This data was cached :"
                Thread {
                    val random = Random()
                    println(random.nextInt(activityDao.count()))
                    message +=
                        activityDao.loadById(random.nextInt(activityDao.count())).description
                }.start()
                findViewById<TextView>(R.id.textView).text = message
                }
        })
    }
   }

    data class BoredActivity(val activity: String)

    interface BoredApi {
        @GET("activity")
        fun getActivity(): Call<BoredActivity>
    }