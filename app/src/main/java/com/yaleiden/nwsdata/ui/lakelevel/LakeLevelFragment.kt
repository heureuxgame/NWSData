package com.yaleiden.nwsdata.ui.lakelevel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yaleiden.nwsdata.databinding.FragmentNotificationsBinding

class LakeLevelFragment : Fragment() {

    private val TAG: String = "LakeLevelFragment"
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView ")
        val lakeLevelViewModel =
            ViewModelProvider(this).get(LakeLevelViewModel::class.java)
        Log.d(TAG, "_binding ")
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        lakeLevelViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val lakeLevelWebView = binding.lakeLevelWebView
        lakeLevelWebView.loadUrl("https://waterdata.usgs.gov/nwisweb/graph?agency_cd=USGS&site_no=02193900&parm_cd=00062&period=7")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}