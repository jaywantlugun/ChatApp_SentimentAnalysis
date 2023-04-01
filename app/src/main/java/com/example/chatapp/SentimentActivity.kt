package com.example.chatapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.util.concurrent.ScheduledThreadPoolExecutor

class SentimentActivity : AppCompatActivity() {

    var messageList: ArrayList<Messages?> = ArrayList()
    lateinit var Sender_uid:String
    lateinit var Receiver_uid:String

    var Sender_message_count:Int = 0
    var Sender_positive_message_count:Int = 0
    var Sender_negative_message_count:Int = 0
    var Sender_neutral_message_count:Int = 0

    var Receiver_message_count:Int = 0
    var Receiver_positive_message_count:Int = 0
    var Receiver_negative_message_count:Int = 0
    var Receiver_neutral_message_count:Int = 0

    lateinit var executor:ScheduledThreadPoolExecutor
    lateinit var nlClassifier: NLClassifier

    lateinit var pieChart: PieChart
    lateinit var pieChart1: PieChart
    lateinit var pieChart2: PieChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sentiment)

        pieChart = findViewById(R.id.pieChart)
        pieChart1 = findViewById(R.id.pieChart1)
        pieChart2 = findViewById(R.id.pieChart2)


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
        entries.add(PieEntry((Sender_positive_message_count+Receiver_positive_message_count).toFloat()))
        entries.add(PieEntry((Sender_neutral_message_count+Receiver_neutral_message_count).toFloat()))
        entries.add(PieEntry((Sender_negative_message_count+Receiver_negative_message_count).toFloat()))

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

        messageList = intent.getSerializableExtra("message_list") as ArrayList<Messages?>
        Sender_uid = intent.getStringExtra("sender_uid").toString()
        Receiver_uid = intent.getStringExtra("receiver_uid").toString()

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
            else{
                Receiver_message_count+=1
                executor.execute {
                    val result = nlClassifier.classify(data.message.toString())
                    if(result[1].score>0.7){
                        Receiver_positive_message_count+=1
                    }
                    else if(result[1].score>=0.3 && result[1].score<=0.7){
                        Receiver_neutral_message_count+=1
                    }
                    else if(result[1].score<0.3){
                        Receiver_negative_message_count+=1
                    }
                }
            }
        }

        loadIntoPieChart()
        loadIntoPieChart1()
        loadIntoPieChart2()

    }

    private fun loadIntoPieChart2() {
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart2.setUsePercentValues(true)
        pieChart2.getDescription().setEnabled(false)
        pieChart2.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart2.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart2.setDrawHoleEnabled(true)
        pieChart2.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart2.setTransparentCircleColor(Color.WHITE)
        pieChart2.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart2.setHoleRadius(58f)
        pieChart2.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart2.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart2.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart2.setRotationEnabled(true)
        pieChart2.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart2.animateY(1400, Easing.EasingOption.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart2.legend.isEnabled = false
        pieChart2.setEntryLabelColor(Color.WHITE)
        pieChart2.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry((Receiver_positive_message_count).toFloat()))
        entries.add(PieEntry((Receiver_neutral_message_count).toFloat()))
        entries.add(PieEntry((Receiver_negative_message_count).toFloat()))

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
        data.setValueTextColor(Color.BLACK)
        pieChart2.setData(data)

        // undo all highlights
        pieChart2.highlightValues(null)

        // loading chart
        pieChart2.invalidate()
    }

    private fun loadIntoPieChart1() {
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart1.setUsePercentValues(true)
        pieChart1.getDescription().setEnabled(false)
        pieChart1.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart1.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart1.setDrawHoleEnabled(true)
        pieChart1.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart1.setTransparentCircleColor(Color.WHITE)
        pieChart1.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart1.setHoleRadius(58f)
        pieChart1.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart1.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart1.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart1.setRotationEnabled(true)
        pieChart1.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart1.animateY(1400, Easing.EasingOption.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart1.legend.isEnabled = false
        pieChart1.setEntryLabelColor(Color.WHITE)
        pieChart1.setEntryLabelTextSize(12f)

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
        data.setValueTextColor(Color.BLACK)
        pieChart1.setData(data)

        // undo all highlights
        pieChart1.highlightValues(null)

        // loading chart
        pieChart1.invalidate()
    }

    companion object{
        const val MODEL_FILE = "model.tflite"
    }

    override fun onResume() {
        super.onResume()

        evaluateMessages()

    }


}