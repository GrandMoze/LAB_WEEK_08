package com.example.lab_week_08

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker

class MainActivity : AppCompatActivity() {
    //Create an instance of work manager
    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Create network constraints
        val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val id = "001"

        //Create work requests
        val firstRequest = OneTimeWorkRequest
            .Builder(FirstWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(FirstWorker.INPUT_DATA_ID, id))
            .build()

        val secondRequest = OneTimeWorkRequest
            .Builder(SecondWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(SecondWorker.INPUT_DATA_ID, id))
            .build()

        //Set up work sequence
        workManager.beginWith(firstRequest)
            .then(secondRequest)
            .enqueue()

        //Observe work results
        workManager.getWorkInfoByIdLiveData(firstRequest.id)
            .observe(this) { info ->
                if (info?.state?.isFinished == true) {
                    showResult("First process is done")
                }
            }

        workManager.getWorkInfoByIdLiveData(secondRequest.id)
            .observe(this) { info ->
                if (info?.state?.isFinished == true) {
                    showResult("Second process is done")
                }
            }
    }

    //Build input data
    private fun getIdInputData(idKey: String, idValue: String) =
        Data.Builder()
            .putString(idKey, idValue)
            .build()

    //Show result as toast
    private fun showResult(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
