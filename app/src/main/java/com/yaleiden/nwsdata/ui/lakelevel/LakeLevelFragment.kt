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

class LakeLevelFragment : Fragment() {

    private val tag: String = "LakeLevelFragment"
    private var _binding: FragmentLakeLevelNewBinding? = null
    private val binding get() = _binding!!

    private lateinit var lakeLevelViewModel: LakeLevelViewModel

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

        // Setup Observers for live UI data changes
        setupObservers()

        // Make initial load operation call natively via the viewmodel layer wrapper
        lakeLevelViewModel.refreshLakeMetrics()

        // Setup Floating Action Button click actions to trigger clean refreshes
        binding.fabBtn.setOnClickListener {
            Log.d(tag, "onClickRefresh: Reloading layout table records")
            lakeLevelViewModel.refreshLakeMetrics()
        }
    }

    private fun setupObservers() {
        lakeLevelViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        lakeLevelViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            binding.textNotifications.text = message
            binding.textNotifications.visibility = if (message.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        }

        lakeLevelViewModel.latestLevel.observe(viewLifecycleOwner) { levelStr ->
            binding.textLatestLevel.text = "Latest: $levelStr"
        }

        lakeLevelViewModel.uiState.observe(viewLifecycleOwner) { uiData ->
            binding.tableLakeData.removeAllViews()
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