package com.example.myapplication

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var newRecyclerView : RecyclerView
    private lateinit var searchingView: SearchView
//    private lateinit var sortedMap: MutableMap<String, MutableList<Request>>
    private var dataList = arrayListOf<Request>()
    private lateinit var theAdapter: RecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newRecyclerView = findViewById(R.id.RecView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        //Show initial data upon load
        fetchDataFromAPI().start()

        //Search for data
        searchingView = findViewById(R.id.searchView)
        searchingView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList(newText)
                return true
            }
        })

        //night/day mode button listen events
        val sharePref = getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = sharePref.edit()
        val isNightMode = sharePref.getBoolean("NIGHT_MODE", false)

        val grayColor = ContextCompat.getColor(this, R.color.my_gray)

        //change to night mode
        val nightModeButton: ImageButton = findViewById(R.id.night_mode)
        val dayModeButton: ImageButton= findViewById(R.id.day_mode)
        val currentMode = GradientDrawable()
        currentMode.shape = GradientDrawable.RECTANGLE
        currentMode.setColor(Color.WHITE)
        currentMode.cornerRadius = 70f

        val unchosenMode = GradientDrawable()
        unchosenMode.cornerRadius = 70f
        unchosenMode.setColor(grayColor)

        nightModeButton.setOnClickListener{
            if (!isNightMode) {
                nightModeButton.background = currentMode
                dayModeButton.background = unchosenMode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("NIGHT_MODE", true)
                editor.apply()
            }else{
                nightModeButton.background = unchosenMode
                dayModeButton.background = currentMode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("NIGHT_MODE", false)
                editor.apply()
            }
        }

        dayModeButton.setOnClickListener{
            if (isNightMode) {
                nightModeButton.background = unchosenMode
                dayModeButton.background = currentMode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("NIGHT_MODE", false)
                editor.apply()
            }
            else{
                nightModeButton.background = currentMode
                dayModeButton.background = unchosenMode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("NIGHT_MODE", true)
                editor.apply()
            }
        }

        //Set font size
        val rootView: ViewGroup = findViewById(android.R.id.content)
        findViewById<Button>(R.id.smallButton).setOnClickListener {
            changeFontSizeRecursively(rootView, -1.5f) // Increase font size by 2sp
        }
        findViewById<Button>(R.id.bigButton).setOnClickListener {
            changeFontSizeRecursively2(rootView, 1.5f) // Increase font size by 2sp
        }
}
        private fun changeFontSizeRecursively(view: View, decreaseBy: Float) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeFontSizeRecursively(view.getChildAt(i), decreaseBy)
            }
        } else if (view is TextView) {
            val currentSize = view.textSize / resources.displayMetrics.scaledDensity
            view.textSize = currentSize + decreaseBy
        }
    }

    private fun changeFontSizeRecursively2(view: View, increaseBy: Float) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeFontSizeRecursively(view.getChildAt(i), increaseBy)
            }
        } else if (view is TextView) {
            val currentSize = view.textSize / resources.displayMetrics.scaledDensity
            view.textSize = currentSize + increaseBy
        }
    }


    private fun filteredList(query: String?){
        if(query != null){
            val filList = ArrayList<Request>()
            for(i in dataList){
                if(i.name.contains(query)){
                    filList.add(i)
                }
            }

            if(filList.isEmpty()){
                Toast.makeText(this,"No data found", Toast.LENGTH_SHORT).show()
            } else{
                theAdapter.setFilteredList(filList)
            }
        }
    }

    private fun fetchDataFromAPI(): Thread
    {
        return Thread{
            val url = URL("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            val conn = url.openConnection() as HttpsURLConnection

            //Runs when connection is successful
            if(conn.responseCode == 200){
                Log.d("TAG", "Connection successful!")
                val inputSystem = conn.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                val listType = object: TypeToken<List<Request>>() {}.type
                val request: List<Request> = Gson().fromJson(inputStreamReader,listType)

                updateUI(request)
                inputStreamReader.close()
                inputSystem.close()

            }
            else{
                Log.d("TAG", "Nope.")
            }
        }
    }

    private fun updateUI(request:List<Request>){
        runOnUiThread {
            kotlin.run {
                val sortedMap = mutableMapOf<String, MutableList<Request>>()

                for(data in request){
                    if(data.name == null || data.name.trim() == "") continue
                    val currMap = Request(data.id,data.name,data.listId)
                    if(data.listId !in sortedMap){
                        sortedMap[data.listId] = mutableListOf(currMap)
                    }
                    else {
                        sortedMap[data.listId]!!.add(currMap)
                    }
                }

                for ((_, datas) in sortedMap) {
                    // Reassign the sorted list to the map
                    datas.sortBy { it.name.substringAfter("Item ").toIntOrNull() ?: Int.MAX_VALUE }
                }

                val keySortedMap = sortedMap.toSortedMap()

                for(datas in keySortedMap.values){
                    for(data in datas){
                        dataList.add(data)
                    }
                }
                theAdapter = RecyclerViewAdapter(dataList)
                newRecyclerView.adapter = theAdapter
            }
        }
    }
}
