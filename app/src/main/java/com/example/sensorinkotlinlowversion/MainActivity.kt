package com.example.sensorinkotlinlowversion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensor:Sensor
    private lateinit var sensorManager: SensorManager

    private var readable :Boolean = true

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        readable = (p1 == SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        var info = ""
        val vs = p0!!.values
        var n = 0
        if(readable){
            for(v in vs){
                info += "value - " + n.toString() + " : " + v.toString() + "\r\n"
                n += 1
            }
        }else{
            info += "accuracy : " + p0.accuracy + "\r\n"
            for(v in vs){
                info += "value - " + n.toString() + " : " + v.toString() + "\r\n"
                n += 1
            }
        }
        values.text = info
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //获取传感器管理器
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //获取传感器列表
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensor = sensors[0]
        //提取传感器名称及列表元素赋值
        val names = ArrayList<String>()
        for(_sensor in sensors){
            names.add(_sensor.name)
        }
        list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names)

        list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var _sensor = sensor
                sensor = sensors.get(p3.toInt())
                var info = " Sensor Name: " + sensor.name + "\r\n"
                info += " Sensor Type: " + sensor.type + "\r\n"
                info += " Sensor Vendor: " + sensor.vendor + "\r\n"
                info += " Sensor Version: " + sensor.version + "\r\n"
                info += " Max Range: " + sensor.maximumRange + "\r\n"
                info += " Min Delay: " + sensor.minDelay + "\r\n"
                info += " Power Consumption: " + sensor.power + "\r\n"
                info += " Resolution: " + sensor.resolution + "\r\n"
                text.text = info

                if(_sensor !== sensor){
                    sensorManager.unregisterListener(this@MainActivity)
                    sensorManager.registerListener(this@MainActivity, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun onPause(){
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onResume(){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }
}
