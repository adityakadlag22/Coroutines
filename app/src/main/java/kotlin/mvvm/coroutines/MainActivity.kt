package kot.mvvm.coroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progressBtn
import kotlinx.android.synthetic.main.activity_main.text_job
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private val result1 = "RESULT1"
    private val timeout = 2100L
    private val progressMax = 100
    private val progressStart = 0
    private val jobTime = 3000
    private lateinit var job: CompletableJob
    private val tag = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        clickMe.setOnClickListener {
            CoroutineScope(IO).launch { fakeApiRequest() }

        }


        progressBtn.setOnClickListener {
            if (!::job.isInitialized) {
                init()
            }
            progressbar.startJobOrCancel(job)

            nextActBtn.setOnClickListener {
                joinFakeApiRequest()
            }
        }

        goToHashMap.setOnClickListener {
            val intent = Intent(this, HashMapDemo::class.java)
            startActivity(intent)
        }


    }

    private fun joinFakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    println("launching job 1 on thread ${Thread.currentThread().name}")
                    getResult1FromApi()
                }.await()

                val result2 = async {
                    println("launching job 2 on thread ${Thread.currentThread().name}")
                    try {
                        getResult1FromApiForJoin(result1)
                    } catch (e: CancellationException) {
                        e.message
                    }
                }.await()
                println("Result 2= $result2")
            }
            println("Total join tasks time is $executionTime ms")
        }

    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0)
            resetJob()
        else {
            updateTextWithJob(R.string.Cancelj.toString())
            CoroutineScope(IO + job).launch {
                println("Job is Active $this is coroutine Job == $job")

                for (i in progressStart..progressMax) {
                    delay(jobTime / progressMax.toLong())
                    this@startJobOrCancel.progress = i
                }
                updateTextWithJob("Job is Complete")
            }
        }
    }

    private fun updateTextWithJob(text: String) {
        GlobalScope.launch(Main) {
            val rest = "Restart"
            text_job.text = text
            progressBtn.text = rest
            logThread(text)
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting Job"))
            init()
        }
    }

    private fun init() {
        this.job = Job()
        val st = "Start"
        progressBtn.text = st
        job.invokeOnCompletion { it ->
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknown Cancellation Error"
                }
                println("$job was Cancelled due to \n $msg")
                showToast(msg)
            }
        }
        progressbar.max = progressMax
        progressbar.progress = progressStart
    }

    private suspend fun setTextToUI(input: String) {
        withContext(Main) {
            val call = text2.text.toString() + "\n$input"
            text2.text = call
        }
    }

    private fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun fakeApiRequest() {


        withContext(IO) {

            val alljobs = CoroutineScope(IO).launch()
            {
                val totalTime = measureTimeMillis {
                    val job = withTimeoutOrNull(timeout) {
                        val res = getResult1FromApi()
                        println("Result 1 : $res")
                        setTextToUI(res)

                        val res2 = getResult1FromApi2()
                        println("Result 1 : $res")
                        setTextToUI(res2)

                    }
                    val job2 = withTimeoutOrNull(timeout) {
                        val res = getResult1FromApi()
                        println("Result 1 : $res")
                        setTextToUI(res)

                        val res2 = getResult1FromApi2()
                        println("Result 1 : $res")
                        setTextToUI(res2)

                    }

                    if (job == null || job2 == null) {
                        setTextToUI("Time Out")
                    }
                }
                Log.d(tag, "fakeApiRequest: TIME $totalTime")
            }
            alljobs.invokeOnCompletion {
                logThread("All jobs completed and time take ")

            }
        }


    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return result1
    }

    private suspend fun getResult1FromApi2(): String {
        logThread("getResult1FromApi2")
        delay(1000)
        return "RESULT2"
    }

    private suspend fun getResult1FromApiForJoin(res: String): String {
        logThread("getResult1FromApi2")
        delay(1000)
        if (res == "RESULT1") {
            return "RESULT2"
        } else {
            throw CancellationException("No Result")
        }

    }

    private fun logThread(text: String) {
        println("debug : $text : ${Thread.currentThread().name}")
    }
}