package com.yaleiden.nwsdata.ui.forecast

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yaleiden.nwsdata.ForecastHourlyData
import com.yaleiden.nwsdata.R
import com.yaleiden.nwsdata.databinding.FragmentWeatherBinding
import com.yaleiden.nwsdata.databinding.ItemWeatherHourlyBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ForecastFragment : Fragment() {

    private val TAG: String = "ForecastFragment"
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val forecastViewModel: ForecastViewModel by viewModels()
    private val hourlyAdapter = HourlyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Leverage ViewBinding completely for RecyclerView configuration
        binding.recyclerview.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Setup Observers
        observeViewModel()

        // Sync Refresh Fab
        binding.fabBtn.setOnClickListener {
            forecastViewModel.getNwsHourlyForecast()
        }

        // Observe the selection index adjustments safely
        forecastViewModel.locationIndex.observe(viewLifecycleOwner) { index ->
            binding.textHome.setSelection(index)
        }

        // Listener to update the ViewModel when selection updates
        binding.textHome.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                forecastViewModel.setLocationIndex(position)
                Log.d(TAG, "locationSpinner position = $position")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Observe URL changes to fetch fresh data
        forecastViewModel.selectedForecastUrl.observe(viewLifecycleOwner) { url ->
            Log.d(TAG, "Selected Forecast URL changed to: $url")
            forecastViewModel.getNwsHourlyForecast()
        }
    }

    private fun observeViewModel() {
        // Toggle global application progress loading indicators
        forecastViewModel.isLoading.observe(viewLifecycleOwner) { isFetching ->
            binding.progressBar.visibility = if (isFetching) View.VISIBLE else View.GONE
        }

        // Clean UI state data delivery observer mapping updates
        forecastViewModel.data.observe(viewLifecycleOwner) { hourlyData ->
            if (!hourlyData.isNullOrEmpty()) {
                // Submit list cleanly without calling notifyDataSetChanged
                hourlyAdapter.submitList(hourlyData)

                // Track sun cycle text configurations cleanly
                binding.sunriseTv.text = forecastViewModel.suntime.value.toString()
                showSunriseAttribution()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showSunriseAttribution() {
        Snackbar.make(binding.root, "Powered by SunriseSunset.io", Snackbar.LENGTH_LONG).show()
    }

    /* --- Clean, Isolated Adapter Using ViewBinding Inside the ViewHolder --- */
    class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastHourlyData>() {
        override fun areItemsTheSame(oldItem: ForecastHourlyData, newItem: ForecastHourlyData) = oldItem.number == newItem.number
        override fun areContentsTheSame(oldItem: ForecastHourlyData, newItem: ForecastHourlyData) = oldItem == newItem
    }

    private inner class HourlyAdapter : ListAdapter<ForecastHourlyData, HourlyViewHolder>(ForecastDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
            val binding = ItemWeatherHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HourlyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
            val listData = currentList[position]
            val context = holder.itemView.context

            val dayHour = try {
                val parsedTime = OffsetDateTime.parse(listData.startTime)
                "${parsedTime.dayOfWeek.toString().take(3)}   ${parsedTime.format(DateTimeFormatter.ofPattern("ha"))}"
            } catch (e: Exception) {
                "N/A"
            }

            holder.binding.apply {
                textViewTemperature.text = "${listData.temperature} / ${listData.relativeHumidity}%"
                textViewWindSpeed.text = listData.windSpeed
                textViewWindDirection.text = listData.windDirection
                textViewStartTime.text = dayHour
                textViewShortForecast.text = "${listData.shortForecast} ${listData.probabilityOfPrecipitation}%"

                val cleanImageName = checkForComma(listData.icon)
                imageViewIcon.setImageDrawable(getDrawableByName(cleanImageName, context))
            }
        }

        private fun getDrawableByName(name: String, context: Context): Drawable? {
            val cleanName = name.replace('-', '_')
            var resId = context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            if (resId == 0) resId = context.resources.getIdentifier("ic_missing", "drawable", context.packageName)
            return AppCompatResources.getDrawable(context, resId)
        }

        private fun checkForComma(icon: String): String {
            val name = if (icon.contains(",")) icon.substringBefore(",") else icon
            return name.replace('-', '_')
        }
    }

    class HourlyViewHolder(val binding: ItemWeatherHourlyBinding) : RecyclerView.ViewHolder(binding.root)
}