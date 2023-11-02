package com.example.mutualmind

import VolleySingleTon
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mutualmind.Model.FirebaseDataModel
import com.example.mutualmind.Model.FirebaseUserDataModel
import com.example.mutualmind.Model.FundDetails
import com.example.mutualmind.Model.PriceData
import com.example.mutualmind.databinding.ActivityFundInfoBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FundInfo : AppCompatActivity() {
    lateinit var binding: ActivityFundInfoBinding
    private var data = ArrayList<FundDetails>()
    var fundCode: String? = null
    var fundName: String? = null
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFundInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val intent = intent
        fundName = intent.getStringExtra("fundName")
        fundCode = intent.getStringExtra("fundCode")

        val funFundName = findViewById<TextView>(R.id.fun_fund_name)

        funFundName.text = fundName

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            fetchFundDetails(fundCode)
        }


        binding.funAddToFolioBtn.setOnClickListener {
            val nav = binding.funNavEdt.text.toString()
            val amount = binding.funAmountEdt.text.toString()
            val NAV = nav.toLong()
            val investment = amount.toLong()
            val units = investment / NAV.toInt()
            val coScope = CoroutineScope(Dispatchers.IO)
            coScope.launch {
                updateUi(
                    nav, amount, units.toString(), fundCode.toString(),
                    fundName.toString()
                )
            }


            Log.d("user", "onCreate:$units ")
        }

    }

    private fun createChar(oneWeek: ArrayList<Float>) {
        val chart = findViewById<LineChart>(R.id.chart)
        val entries: MutableList<Entry> = ArrayList()
        for (i in 0 until oneWeek.size) {
            val index: Float = 0f
            entries.add(Entry(oneWeek[i], index))
        }
//        entries.add(Entry(0f, 4f))
//        entries.add(Entry(1f, 3f))
//        entries.add(Entry(2f, 2f))
//        entries.add(Entry(3f, 1f))
//        entries.add(Entry(4f, 5f))
//        entries.add(Entry(5f, 4f))
//        entries.add(Entry(6f, 6f))

        val linedataset = LineDataSet(entries, "First")
        linedataset.color = resources.getColor(R.color.black)
        linedataset.setDrawFilled(true)
        linedataset.fillDrawable = ContextCompat.getDrawable(this, R.drawable.gradient)
        val data = LineData(linedataset)

        linedataset.lineWidth = 2F;
        linedataset.circleRadius = 6F;
        linedataset.setCircleColor(Color.WHITE);
        linedataset.color = R.color.red;
        linedataset.setDrawCircleHole(true);
        linedataset.setDrawCircles(true);
        linedataset.setDrawHorizontalHighlightIndicator(false);
        linedataset.setDrawHighlightIndicators(false);
        chart.data = data
//        chart.animateX(2000)
        chart.animateY(3000)

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.LTGRAY
        xAxis.enableGridDashedLine(16f, 12f, 0f)
        linedataset.circleHoleColor = R.color.red
        val yAxis = chart.axisRight
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawLabels(false)
        chart.invalidate()


        val yAxisLeft: YAxis = chart.axisLeft
        yAxisLeft.setDrawLabels(false)
        yAxisLeft.setDrawAxisLine(false)
        yAxisLeft.setDrawGridLines(false)
        yAxis.spaceTop = 25f
        yAxis.spaceBottom = 25f
        chart.invalidate()

    }

    private fun createChart(oneWeek: ArrayList<PriceData>) {
        val chart = findViewById<LineChart>(R.id.chart)
        val entries: MutableList<Entry> = ArrayList()
//        for (i in 0 until oneWeek.size) {
//            val priceData = oneWeek[i]
//            val entry = Entry(priceData.price.toFloat(), i.toFloat())
//            entries.add(entry)
//        }
        val startingIndex = 1
        for (i in 0 until oneWeek.size) {
            val priceData = oneWeek[i]
            val entry = Entry((i - startingIndex).toFloat(), priceData.price.toFloat())
            entries.add(entry)
        }


        val linedataset = LineDataSet(entries, "First")
        linedataset.color = resources.getColor(R.color.black)
        linedataset.setDrawFilled(true)
        linedataset.fillDrawable = ContextCompat.getDrawable(this, R.drawable.gradient)
        val data = LineData(linedataset)

        linedataset.lineWidth = 1F
        linedataset.circleRadius = 2F
        linedataset.setCircleColor(Color.BLACK)
        linedataset.color = R.color.red
        linedataset.setDrawCircleHole(true)
        linedataset.setDrawCircles(true)
        linedataset.setDrawHorizontalHighlightIndicator(false)
        linedataset.setDrawHighlightIndicators(false)
        chart.data = data
        chart.animateY(3000)

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.LTGRAY
        xAxis.enableGridDashedLine(16f, 12f, 0f)
        linedataset.circleHoleColor = R.color.red
        val yAxis = chart.axisRight
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawGridLines(false)
        yAxis.setDrawLabels(false)
        chart.invalidate()

//        val yAxisLeft: YAxis = chart.axisLeft
//        yAxisLeft.setDrawLabels(false)
//        yAxisLeft.setDrawAxisLine(false)
//        yAxisLeft.setDrawGridLines(false)
//        yAxis.spaceTop = 25f
//        yAxis.spaceBottom = 25f
        val yAxisLeft: YAxis = chart.axisLeft
        yAxisLeft.setDrawLabels(true)
        yAxisLeft.textColor = Color.LTGRAY
        chart.invalidate()
    }

    private suspend fun updateUi(
        nav: String,
        amount: String,
        units: String,
        fundCode: String,
        fundName: String,
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            addDataToDatabase(
                nav,
                amount,
                units,
                fundCode,
                fundName
            )
        }

    }


    private suspend fun addDataToDatabase(
        nav: String,
        amount: String,
        units: String,
        fundCode: String,
        fundName: String,
    ) {
        val database = FirebaseDatabase.getInstance().getReference("Users")
        val email = firebaseAuth.currentUser?.email
        database.child(firebaseAuth.currentUser?.uid.toString()).setValue(
            FirebaseUserDataModel(
                email.toString(),
                firebaseAuth.currentUser?.uid.toString()
            )
        )
        val myref =
            FirebaseDatabase.getInstance().getReference(firebaseAuth.currentUser?.uid.toString())
        myref.child(fundName).setValue(FirebaseDataModel(nav, amount, units, fundName, fundCode))
            .addOnCompleteListener {
                binding.funNavEdt.text = null
                binding.funAmountEdt.text = null
            }
    }

    private fun fetchFundDetails(fundCode: String?) {
        val url = "https://api.mfapi.in/mf/$fundCode"
        val jsonObject = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

            val details = it.getJSONObject("meta")
            val fundHouse = details.getString("fund_house")
            val category = details.getString("scheme_category")
            val schemeName = details.getString("scheme_name")
            val priceData = ArrayList<PriceData>()
            val jsonArray = it.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val date = jsonObject.getString("date")
                val nav = jsonObject.getString("nav")
                val info = PriceData(date, nav)
                priceData.add(info)
            }
            calculateReturns(priceData)
            val nav = priceData[0].price
            val funFundName = findViewById<TextView>(R.id.fun_fund_name)
            funFundName.text = schemeName
            val funCategory = findViewById<TextView>(R.id.fun_category)
            funCategory.text = category
            val funFundHouse = findViewById<TextView>(R.id.fun_fund_house)
            funFundHouse.text = fundHouse
            val funNav = findViewById<TextView>(R.id.fun_nav)
            funNav.text = nav
//            Log.d("REST RES", " $priceData")
        }) { error ->
            Log.d("REST RES", "error $error")

        }
        VolleySingleTon.getInstance(applicationContext).addToRequestQueue(jsonObject)
    }

    private fun calculateReturns(priceData: ArrayList<PriceData>) {
        val oneWeek = ArrayList<PriceData>()
        val oneMonth = ArrayList<PriceData>()
        val threeMonths = ArrayList<PriceData>()
        val sixMonths = ArrayList<PriceData>()
        val oneYear = ArrayList<PriceData>()
        val oneWeekVal = ArrayList<Float>()
        for (i in 0 until 7) {
            oneWeek.add(priceData[i]);
            oneWeekVal.add(priceData[i].price.toFloat())
        }
        createChart(priceData)
        for (i in 0 until 30) {
            oneMonth.add(priceData.get(i))
        }
        for (i in 0 until 90) {
            threeMonths.add(priceData.get(i))
        }
        for (i in 0 until 180) {
            sixMonths.add(priceData.get(i))
        }
        for (i in 0 until 365) {
            oneYear.add(priceData[i])
        }
        val oneDayBtn = findViewById<TextView>(R.id.oneDay)
        val oneWeekBtn = findViewById<TextView>(R.id.oneWeek)
        val oneMonthBtn = findViewById<TextView>(R.id.oneMonth)
        val threeMonthsBtn = findViewById<TextView>(R.id.threeMonths)
        val sixMonthsBtn = findViewById<TextView>(R.id.sixMonths)
        val oneYearBtn = findViewById<TextView>(R.id.oneYear)

        // calculate return for one week
        val oneWeekProfit = priceData.get(0).price.toDouble() - priceData.get(7).price.toDouble()
        val oneWeekReturn = (oneWeekProfit / oneWeek.get(0).price.toDouble()) * 100
        val oneWeekFinal = String.format("%.1f", oneWeekReturn).toDouble()
        oneWeekBtn.text = "$oneWeekFinal%"


        // calculate return for one day
        val oneDayProfit = priceData.get(0).price.toDouble() - priceData.get(1).price.toDouble()
        val oneDayReturn = (oneDayProfit / priceData.get(0).price.toDouble()) * 100
        val oneDayFinal = String.format("%.1f", oneDayReturn).toDouble()
        oneDayBtn.text = "$oneDayFinal%"

        // calculate return for one month
        val oneMonthProfit = priceData.get(0).price.toDouble() - priceData.get(30).price.toDouble()
        val oneMonthReturn = (oneMonthProfit / priceData.get(0).price.toDouble()) * 100
        val oneMonthFinal = String.format("%.1f", oneMonthReturn).toDouble()
        oneMonthBtn.text = "$oneMonthFinal%"

        //calculate return for 3 month
        val threeMonthProfit =
            priceData.get(0).price.toDouble() - priceData.get(90).price.toDouble()
        val threeMonthReturn = (threeMonthProfit / priceData.get(0).price.toDouble()) * 100
        val threeMonthFinal = String.format("%.1f", threeMonthReturn).toDouble()
        threeMonthsBtn.text = "$threeMonthFinal%"


        //calculate return for 6 months
        val sixMonthProfit = priceData.get(0).price.toDouble() - priceData.get(180).price.toDouble()
        val sixMonthReturn = (sixMonthProfit / priceData.get(0).price.toDouble()) * 100
        val sixMonthFinal = String.format("%.1f", sixMonthReturn).toDouble()
        sixMonthsBtn.text = "$sixMonthFinal%"

        // calculate return for 1year
        val oneYearProfit = priceData.get(0).price.toDouble() - priceData.get(365).price.toDouble()
        val oneYearReturn = (oneYearProfit / priceData.get(0).price.toDouble()) * 100
        val oneYearFinal = String.format("%.1f", oneYearReturn).toDouble()
        oneYearBtn.text = "$oneYearFinal%"

        setTrendColor(oneDayReturn > 0, oneDayBtn)
        setTrendColor(
            oneWeek.get(oneWeek.size - 1).price.toDouble() > oneWeek.get(0).price.toDouble(),
            oneWeekBtn
        )
        setTrendColor(
            oneMonth.get(oneMonth.size - 1).price.toDouble() > oneMonth.get(0).price.toDouble(),
            oneMonthBtn
        )
        setTrendColor(
            threeMonths.get(threeMonths.size - 1).price.toDouble() > threeMonths.get(0).price.toDouble(),
            threeMonthsBtn
        )
        setTrendColor(
            sixMonths.get(sixMonths.size - 1).price.toDouble() > sixMonths.get(0).price.toDouble(),
            sixMonthsBtn
        )
        setTrendColor(
            oneYear.get(oneYear.size - 1).price.toDouble() > oneYear.get(0).price.toDouble(),
            oneYearBtn
        )
//        if (oneWeek.get(oneWeek.size - 1).price.toDouble() > oneWeek.get(0).price.toDouble()) {
//            b1.setBackgroundColor(Color.RED)
//
//        } else {
//            b1.setBackgroundColor(Color.parseColor("#18BC28"))
//        }
    }


    private fun setTrendColor(b: Boolean, b1: TextView) {
        if (!b) {
            //negative
            b1.setTextColor(Color.RED)
        } else {
            //positive
            b1.setTextColor(Color.parseColor("#18BC28"))
        }
    }
}