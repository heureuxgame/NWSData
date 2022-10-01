package com.yaleiden.nwsdata.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yaleiden.nwsdata.ForecastHourlyData
import com.yaleiden.nwsdata.R
import com.yaleiden.nwsdata.databinding.FragmentHomeBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    private val TAG: String = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var hourlyData: List<ForecastHourlyData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerview)
        val adapter = HourlyAdapter()

        homeViewModel.data.observe(viewLifecycleOwner) {
            hourlyData = it
            val text_home: TextView = root.findViewById(R.id.text_home)
            if (hourlyData != null){
            adapter.submitList(hourlyData)
                text_home.text = "Yale's House"
            }else{

                text_home.text = "Refresh Data"
            }

        }

        recyclerView.adapter = adapter
        recyclerView.setLayoutManager(LinearLayoutManager(context));

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private inner class HourlyAdapter :
        ListAdapter<ForecastHourlyData, HourlyViewHolder>(ForecastHourlyData.DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
            Log.d("HomeFragment", "HourlyAdapter onCreateViewHolder")
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_weather_hourly, parent, false)
            return HourlyViewHolder(view)
        }

        override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
            Log.d("HomeFragment", "HourlyAdapter onBindViewHolder")

            val listData = currentList.get(position)
            //2022-09-15T17:00:00-04:00  Example of time date from JSON
            var dayHour: String = try {
                //Get day of week, take the first three letters, add a space
                OffsetDateTime.parse(listData.startTime).dayOfWeek.toString().take(3).plus(" ")
                        //Get offset time to correct UTC to local
                    .plus(OffsetDateTime.parse(listData.startTime)
                    //Format time to show hour and am/pm
                    .format(DateTimeFormatter.ofPattern("  ha")).toString())

            } catch (e: Exception) {
                Log.d("HomeFragment", "onBindViewHolder " + e.toString())
                e.toString()
            }

            holder.textView_windSpeed.text = listData.windSpeed
            holder.textView_temperature.text = listData.temperature.toString().plus("F")
            holder.textView_windDirection.text = listData.windDirection
            holder.textView_startTime.text = dayHour
            holder.textView_shortForecast.text = listData.shortForecast
            val imageName = checkForComma(listData.icon)

            holder.imageViewIcon.setImageDrawable(getDrawableByName(imageName, context!!))
            //Log.d("HomeFragment", "onBindViewHolder " + stuff.icon)
        }

        //
        fun getDrawableByName(name: String, context: Context): Drawable? {
            Log.d("HomeFragment", "getDrawableByName " + name)
            val drawableResource =
                context.resources.getIdentifier(name, "drawable", context.packageName)
            if (drawableResource == 0) {
                throw RuntimeException("Can't find drawable with name: $name")
            }
            return ContextCompat.getDrawable(activity!!, drawableResource)
        }

        fun checkForComma(icon: String): String {
            //a typical link to an icon is like this
            //https://api.weather.gov/icons/land/day/rain_showers?size=small
            //Sometimes like this, to show a 30% chance
            //https://api.weather.gov/icons/land/day/rain_showers,30?size=small
            //The HomeViewModel class has trimmed away all but the icon name "rain_showers"
            //Icon names are the same as drawable names
            //But, the ",30" is not part of the name
            //Maybe I'll add functionality to add "30%" over the drawable

            var name = icon
            var percentage = "0"
            if (name.contains(",")) {
                name = icon.substringBefore(",")
                // this is the percentage chance, but NOT part of the drawable name
                percentage = icon.substringAfter("'")
            }
            return name
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

    }
}