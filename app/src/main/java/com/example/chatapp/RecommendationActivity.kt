package com.example.chatapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.database.*
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.util.concurrent.ScheduledThreadPoolExecutor

class RecommendationActivity : AppCompatActivity() {

    var messageList: ArrayList<Messages?> = ArrayList()
    lateinit var Sender_uid:String

    var Sender_message_count:Int = 0
    var Sender_positive_message_count:Int = 0
    var Sender_negative_message_count:Int = 0
    var Sender_neutral_message_count:Int = 0


    lateinit var executor: ScheduledThreadPoolExecutor
    lateinit var nlClassifier: NLClassifier

    lateinit var pieChart: PieChart

    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = firebaseDatabase.reference.child("UsersList")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        pieChart = findViewById(R.id.pieChart)


        val options = NLClassifier.NLClassifierOptions.builder().build()
        nlClassifier = NLClassifier.createFromFileAndOptions(
            this,MODEL_FILE,options
        )

        executor = ScheduledThreadPoolExecutor(1)


        evaluateMessages()


    }



    //---------------------------------------------------

    private fun loadIntoPieChart() {
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry((Sender_positive_message_count).toFloat()))
        entries.add(PieEntry((Sender_neutral_message_count).toFloat()))
        entries.add(PieEntry((Sender_negative_message_count).toFloat()))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
    }

    //----------------------------------------------------------------------

    private fun evaluateMessages() {
        Sender_uid = intent.getStringExtra("userid").toString()
        messageList = intent.getSerializableExtra("message_list") as ArrayList<Messages?>
        //Toast.makeText(this@RecommendationActivity,messageList.size.toString(),Toast.LENGTH_LONG).show()

        for (data in messageList!!){
            if(data!!.uid==Sender_uid){
                Sender_message_count+=1
                executor.execute {
                    val result = nlClassifier.classify(data.message.toString())
                    if(result[1].score>0.6){
                        Sender_positive_message_count+=1
                    }
                    else if(result[1].score>=0.4 && result[1].score<=0.6){
                        Sender_neutral_message_count+=1
                    }
                    else if(result[1].score<0.4){
                        Sender_negative_message_count+=1
                    }
                }
            }

        }

        loadIntoPieChart()

    }

    companion object{
        const val MODEL_FILE = "model.tflite"
    }

    override fun onResume() {
        super.onResume()

        evaluateMessages()

    }


}