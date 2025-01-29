package com.yaleiden.nwsdata.ui.radar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yaleiden.nwsdata.databinding.FragmentRadarBinding



class RadarFragment : Fragment() {
    private val TAG = "RadarFragment"
    private var _binding: FragmentRadarBinding? = null
    //private var radarWebView: WebView? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val radarViewModel =
            ViewModelProvider(this).get(RadarViewModel::class.java)

        _binding = FragmentRadarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        radarViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val radarWebView = binding.radarWebView
        radarWebView.settings.javaScriptEnabled = true
        // Standard low bandwidth gif KCAE
        //val urlstring = "https://radar.weather.gov/ridge/standard/KCAE_loop.gif"
        // Standard low bandwidth gif Southeast
        //val urlstring = "https://radar.weather.gov/ridge/standard/SOUTHEAST_loop.gif"
        // Enhanced radar
        //val urlstring = "https://radar.weather.gov/?settings=v1_eyJhZ2VuZGEiOnsiaWQiOiJsb2NhbCIsImNlbnRlciI6Wy04MS43NDksMzIuNTE5XSwibG9jYXRpb24iOm51bGwsInpvb20iOjcsImZpbHRlciI6bnVsbCwibGF5ZXIiOiJicmVmX3JhdyIsInN0YXRpb24iOiJLQ0FFIn0sImFuaW1hdGluZyI6ZmFsc2UsImJhc2UiOiJzdGFuZGFyZCIsImFydGNjIjpmYWxzZSwiY291bnR5IjpmYWxzZSwiY3dhIjpmYWxzZSwicmZjIjpmYWxzZSwic3RhdGUiOmZhbHNlLCJtZW51Ijp0cnVlLCJzaG9ydEZ1c2VkT25seSI6dHJ1ZSwib3BhY2l0eSI6eyJhbGVydHMiOjAuOCwibG9jYWwiOjAuNiwibG9jYWxTdGF0aW9ucyI6MC44LCJuYXRpb25hbCI6MC42fX0%3D#/"
        // Centered off Winfield CG with map marker
        //val urlstring = "https://radar.weather.gov/?settings=v1_eyJhZ2VuZGEiOnsiaWQiOiJ3ZWF0aGVyIiwiY2VudGVyIjpbLTgyLjQyOCwzMy42NTldLCJsb2NhdGlvbiI6Wy04Mi40MjgsMzMuNjU5XSwiem9vbSI6MTIsImxheWVyIjoiYnJlZl9xY2QifSwiYW5pbWF0aW5nIjpmYWxzZSwiYmFzZSI6InN0YW5kYXJkIiwiYXJ0Y2MiOmZhbHNlLCJjb3VudHkiOmZhbHNlLCJjd2EiOmZhbHNlLCJyZmMiOmZhbHNlLCJzdGF0ZSI6ZmFsc2UsIm1lbnUiOnRydWUsInNob3J0RnVzZWRPbmx5IjpmYWxzZSwib3BhY2l0eSI6eyJhbGVydHMiOjAuOCwibG9jYWwiOjAuNiwibG9jYWxTdGF0aW9ucyI6MC44LCJuYXRpb25hbCI6MC42fX0%3D"
        // Centered off Winfield CG with map marker - zoom out
        //val urlstring = "https://radar.weather.gov/?settings=v1_eyJhZ2VuZGEiOnsiaWQiOiJ3ZWF0aGVyIiwiY2VudGVyIjpbLTgyLjQyOSwzMy42NTVdLCJsb2NhdGlvbiI6Wy04Mi40MjgsMzMuNjU5XSwiem9vbSI6MTAsImxheWVyIjoiYnJlZl9xY2QifSwiYW5pbWF0aW5nIjpmYWxzZSwiYmFzZSI6InN0YW5kYXJkIiwiYXJ0Y2MiOmZhbHNlLCJjb3VudHkiOmZhbHNlLCJjd2EiOmZhbHNlLCJyZmMiOmZhbHNlLCJzdGF0ZSI6ZmFsc2UsIm1lbnUiOnRydWUsInNob3J0RnVzZWRPbmx5IjpmYWxzZSwib3BhY2l0eSI6eyJhbGVydHMiOjAuOCwibG9jYWwiOjAuNiwibG9jYWxTdGF0aW9ucyI6MC44LCJuYXRpb25hbCI6MC42fX0%3D"
        // // Winfield CG with map marker - zoom out - Centered west of Winfield
        //val urlstring = "https://radar.weather.gov/?settings=v1_eyJhZ2VuZGEiOnsiaWQiOiJ3ZWF0aGVyIiwiY2VudGVyIjpbLTgyLjY1MiwzMy41ODNdLCJsb2NhdGlvbiI6Wy04Mi40MjgsMzMuNjU5XSwiem9vbSI6MTAsImxheWVyIjoiYnJlZl9xY2QifSwiYW5pbWF0aW5nIjpmYWxzZSwiYmFzZSI6InN0YW5kYXJkIiwiYXJ0Y2MiOmZhbHNlLCJjb3VudHkiOmZhbHNlLCJjd2EiOmZhbHNlLCJyZmMiOmZhbHNlLCJzdGF0ZSI6ZmFsc2UsIm1lbnUiOnRydWUsInNob3J0RnVzZWRPbmx5IjpmYWxzZSwib3BhY2l0eSI6eyJhbGVydHMiOjAuOCwibG9jYWwiOjAuNiwibG9jYWxTdGF0aW9ucyI6MC44LCJuYXRpb25hbCI6MC42fX0%3D"
        val urlstring = "https://radar.weather.gov/?settings=v1_eyJhZ2VuZGEiOnsiaWQiOiJ3ZWF0aGVyIiwiY2VudGVyIjpbLTgyLjUzOSwzMy42MDRdLCJsb2NhdGlvbiI6Wy04Mi40MjgsMzMuNjU5XSwiem9vbSI6MTAsImxheWVyIjoiYnJlZl9xY2QifSwiYW5pbWF0aW5nIjpmYWxzZSwiYmFzZSI6InN0YW5kYXJkIiwiYXJ0Y2MiOmZhbHNlLCJjb3VudHkiOmZhbHNlLCJjd2EiOmZhbHNlLCJyZmMiOmZhbHNlLCJzdGF0ZSI6ZmFsc2UsIm1lbnUiOnRydWUsInNob3J0RnVzZWRPbmx5IjpmYWxzZSwib3BhY2l0eSI6eyJhbGVydHMiOjAuOCwibG9jYWwiOjAuNiwibG9jYWxTdGF0aW9ucyI6MC44LCJuYXRpb25hbCI6MC42fX0%3D"
        radarWebView.loadUrl(urlstring)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}