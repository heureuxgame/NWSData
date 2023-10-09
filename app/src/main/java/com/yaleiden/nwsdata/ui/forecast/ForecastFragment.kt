package com.yaleiden.nwsdata.ui.forecast

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yaleiden.nwsdata.ForecastHourlyData
import com.yaleiden.nwsdata.R
import com.yaleiden.nwsdata.databinding.FragmentHomeBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class ForecastFragment : Fragment() {

    private val TAG: String = "ForecastFragment"
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var hourlyData: List<ForecastHourlyData>
    private val forecastViewModel: ForecastViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView View ")

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerview)
        val adapter = HourlyAdapter()
        val progress: ProgressBar = root.findViewById(R.id.progressBar)
        Log.d(TAG, "before homeViewModel.data.observe ")

        forecastViewModel.data.observe(viewLifecycleOwner) {
            hourlyData = it
            val text_home: TextView = root.findViewById(R.id.text_home)
            if (hourlyData != null) {
                Log.d(TAG, "hourlyData != null " + hourlyData)

                if (hourlyData.isEmpty()) {
                    text_home.text = "Loading forecast"
                } else {
                    Log.d(TAG, "adapter.submitList(hourlyData) ")
                    adapter.submitList(hourlyData)
                    adapter.notifyDataSetChanged()
                    //text_home.text = getString(R.string.loc_name)   //Top UI Banner
                    //text_home.text =  NwsApi.location //Top UI Banner
                    text_home.text =  forecastViewModel.location //Top UI Banner
                    progress.visibility = View.GONE    //Remove progress when loaded
                }
            } else {

                text_home.text = "Refresh Data"
            }

        }

        recyclerView.adapter = adapter
        recyclerView.setLayoutManager(LinearLayoutManager(context));

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: View = requireView().findViewById(R.id.fab_btn)
        fab.setOnClickListener(){
            onClickRefresh()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClickRefresh() {
        forecastViewModel.getNwsHourlyForecast()
    }

    class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastHourlyData>() {
        override fun areItemsTheSame(
            oldItem: ForecastHourlyData,
            newItem: ForecastHourlyData
        ): Boolean {

            Log.d(
                "ForecastDiffCallback",
                " areItemsTheSame " + oldItem.number + " " + newItem.number
            )
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(
            oldItem: ForecastHourlyData,
            newItem: ForecastHourlyData
        ): Boolean {
            Log.d(
                "ForecastDiffCallback",
                " areContentsTheSame " + oldItem.startTime + " " + newItem.startTime
            )
            return oldItem.startTime == newItem.startTime
                    && oldItem.endTime == newItem.endTime
                    && oldItem.shortForecast == newItem.shortForecast
                    && oldItem.temperature == newItem.temperature
                    && oldItem.icon == newItem.icon

        }

        override fun getChangePayload(
            oldItem: ForecastHourlyData,
            newItem: ForecastHourlyData
        ): Any? {
            Log.d("Forecast Fragment DiffUtil getChangePayload", newItem.startTime)
            return super.getChangePayload(oldItem, newItem)
        }
    }

    private inner class HourlyAdapter :
        ListAdapter<ForecastHourlyData, HourlyViewHolder>(ForecastDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
            //Log.d("HomeFragment", "HourlyAdapter onCreateViewHolder")
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_weather_hourly, parent, false)
            return HourlyViewHolder(view)
        }

        override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
            //Log.d("HomeFragment", "HourlyAdapter onBindViewHolder")

            val listData = currentList.get(position)
            //2022-09-15T17:00:00-04:00  Example of time date from JSON
            var dayHour: String = try {
                //Get day of week, take the first three letters, add a space
                OffsetDateTime.parse(listData.startTime).dayOfWeek.toString().take(3).plus(" ")
                    //Get offset time to correct UTC to local
                    .plus(
                        OffsetDateTime.parse(listData.startTime)
                            //Format time to show hour and am/pm
                            .format(DateTimeFormatter.ofPattern("  ha")).toString()
                    )

            } catch (e: Exception) {
                Log.d(TAG, "onBindViewHolder " + e.toString())
                e.toString()
            }

            holder.textView_windSpeed.text = listData.windSpeed
            holder.textView_temperature.text = listData.temperature.toString().plus("F")
            holder.textView_windDirection.text = listData.windDirection
            holder.textView_startTime.text = dayHour
            holder.textView_shortForecast.text = listData.shortForecast.plus(" " +listData.probabilityOfPrecipitation.toString().plus("%"))
            //holder.textView_detailForecast.text = listData.detailedForecast
            val imageName = checkForComma(listData.icon)
            Log.d(TAG, "startTime " + listData.startTime)
            holder.imageViewIcon.setImageDrawable(getDrawableByName(imageName, context!!))
            //Log.d(TAG, "onBindViewHolder detail" + listData.detailedForecast)

        }

        //
        fun getDrawableByName(name: String, context: Context): Drawable? {

            Log.d(TAG, "getDrawableByName " + name)
            var drawableResource =
                context.resources.getIdentifier(name, "drawable", context.packageName)
            /*
            If an icon is not found or is mispelled, add generic icon "ic_missing"
             */
            if (drawableResource == 0) {
                Log.d(TAG, "drawableResource ic_missing " + name)
                //throw RuntimeException("Can't find drawable with name: $name")
                drawableResource =
                    context.resources.getIdentifier("ic_missing", "drawable", context.packageName)
                /* Icons and descrition can be found here.
                Check for spelling.  Descriptions found here https://w1.weather.gov/xml/current_obs/weather.php
                 */
            }
            return ContextCompat.getDrawable(activity!!, drawableResource)
        }

        fun checkForComma(icon: String): String {
            //a typical link to an icon is like this
            //https://api.weather.gov/icons/land/day/rain_showers?size=small
            //Sometimes like this, to show a 30% chance
            //https://api.weather.gov/icons/land/day/rain_showers,30?size=small
            //The ForecastViewModel class has trimmed away all but the icon name "rain_showers"
            //Icon names are the same as drawable names
            //But, the ",30" is not part of the name
            //Maybe I'll add functionality to add "30%" over the drawable
            //Log.d(TAG, "Enhanced " + checkForEnhanced(icon))
            var name = icon
            var percentage = "0"
            if (name.contains(",")) {
                name = icon.substringBefore(",")
                // this is the percentage chance, but NOT part of the drawable name
                percentage = icon.substringAfter(",")
            }
            Log.d(TAG, "Normal " + name)
            return name
        }

        fun checkForEnhanced(icon: String): String {
            //a typical link to an icon is like this
            //https://api.weather.gov/icons/land/day/rain_showers?size=small
            //Sometimes like this, to show a 30% chance
            //https://api.weather.gov/icons/land/day/rain_showers,30?size=small
            //The ForecastViewModel class has trimmed away all but the icon name "rain_showers"
            //Icon names are the same as drawable names
            //But, the ",30" is not part of the name
            //Maybe I'll add functionality to add "30%" over the drawable
            Log.d(TAG, "Enhanced icon " + icon)
            var enhancedame = icon
            var percentage = "0"
            if (enhancedame.contains(",")) {
                enhancedame = icon.substringBefore(",")
                Log.d(TAG, "Enhanced name " + enhancedame)
                // this is the percentage chance, but NOT part of the drawable name
                percentage = icon.substringAfter(",")
                Log.d(TAG, "Enhanced perc " + percentage)
                if (percentage != "0"){
                    enhancedame = enhancedame.plus(percentage)
                }
            }

            return enhancedame
        }


    }

    /**
     * Basic [RecyclerView.ViewHolder] for our gallery.
     */
    class HourlyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        //val rootView = view
        val textView_startTime: TextView = view.findViewById(R.id.textView_startTime)
        val textView_temperature: TextView = view.findViewById(R.id.textView_temperature)
        val textView_windSpeed: TextView = view.findViewById(R.id.textView_windSpeed)
        val textView_windDirection: TextView = view.findViewById(R.id.textView_windDirection)
        val textView_shortForecast: TextView = view.findViewById(R.id.textView_shortForecast)
        val imageViewIcon: ImageView = view.findViewById(R.id.imageViewIcon)
        //val textView_detailForecast: TextView = view.findViewById(R.id.textView_detailForecast)
    }


}