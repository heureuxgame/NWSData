package com.yaleiden.nwsdata.ui.lakelevel

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yaleiden.nwsdata.databinding.FragmentLakeLevelNewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
//import com.yaleiden.nwsdata.BuildConfig

class LakeLevelFragment : Fragment() {

    private val tag: String = "LakeLevelFragment"
    private var _binding: FragmentLakeLevelNewBinding? = null
    private val binding get() = _binding!!

    private lateinit var lakeLevelViewModel: LakeLevelViewModel

    private val newlevelUrl: String = "https://waterservices.usgs.gov/nwis/dv/?site=02193900&format=waterml,1.1&ParameterCd=00062&"
    private val newlevelLatestUrl: String = "https://api.waterdata.usgs.gov/ogcapi/v0/collections/latest-continuous/items?monitoring_location_id=USGS-02193900&parameter_code=00062&f=json"

    //private val api_key = BuildConfig.WEATHER_API_KEY
    private var endDate: String = ""
    private var startDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLakeLevelNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lakeLevelViewModel = ViewModelProvider(this).get(LakeLevelViewModel::class.java)

        // 1) Compute required dates formatted as requested
        calculateDates()

        // 2) Build full parameter URL string
        val loadUrl = newlevelUrl + startDate + endDate
        Log.d(tag, "Target XML API Request: $loadUrl")

        // Setup Observers for UI response processing
        setupObservers()

        // 3) Make initial API load operation for both paths
        lakeLevelViewModel.fetchLakeData(loadUrl, newlevelLatestUrl)

        // Setup Floating Action Button click actions to reload cleanly
        binding.fabBtn.setOnClickListener {
            Log.d(tag, "onClickRefresh: Reloading layout table records")
            lakeLevelViewModel.fetchLakeData(loadUrl, newlevelLatestUrl)
        }
    }

    private fun calculateDates() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val sevenDaysAgoStr = dateFormat.format(calendar.time)

        startDate = "startDT=$sevenDaysAgoStr"
        endDate = "&endDT=$todayStr"
    }

    private fun setupObservers() {
        // Loader handling animation spinner changes
        lakeLevelViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Handle error notice updates
        lakeLevelViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            binding.textNotifications.text = message
            if (message.isNotEmpty()) {
                binding.textNotifications.visibility = View.VISIBLE
            } else {
                binding.textNotifications.visibility = View.INVISIBLE
            }
        }

        // Observe and update the new real-time bold level TextView above table
        lakeLevelViewModel.latestLevel.observe(viewLifecycleOwner) { levelStr ->
            binding.textLatestLevel.text = "Latest: $levelStr"
        }

        // When data returns, build and update the table rows natively
        lakeLevelViewModel.uiState.observe(viewLifecycleOwner) { uiData ->
            binding.tableLakeData.removeAllViews() // Clear layout cleanly before writing elements

            if (uiData != null) {
                binding.textSiteName.text = uiData.siteName
                populateTable(uiData)
            } else {
                binding.textSiteName.text = ""
            }
        }
    }

    private fun populateTable(data: LakeUiData) {
        val context = requireContext()

        // Create Header Row
        val headerRow = TableRow(context).apply {
            setBackgroundColor(Color.GRAY)
            setPadding(0, 10, 0, 10)
        }

        val headers = arrayOf("Date", "Max (ft)", "Min (ft)", "Mean (ft)")
        for (headerText in headers) {
            val textView = TextView(context).apply {
                text = headerText
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                setPadding(8, 8, 8, 8)
            }
            headerRow.addView(textView)
        }
        binding.tableLakeData.addView(headerRow)

        // Combine metric lists based on matching dates (reversed for latest first)
        val dates = data.meanList.map { it.date }.reversed()

        for (date in dates) {
            val maxVal = data.maxList.find { it.date == date }?.value ?: "N/A"
            val minVal = data.minList.find { it.date == date }?.value ?: "N/A"
            val meanVal = data.meanList.find { it.date == date }?.value ?: "N/A"

            val row = TableRow(context).apply {
                setPadding(0, 8, 0, 8)
            }

            val columns = arrayOf(date, maxVal, minVal, meanVal)
            for (colText in columns) {
                val textView = TextView(context).apply {
                    text = colText
                    gravity = Gravity.CENTER
                    setPadding(8, 8, 8, 8)
                }
                row.addView(textView)
            }
            binding.tableLakeData.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}