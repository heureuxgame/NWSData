package com.yaleiden.nwsdata.ui.lakelevel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yaleiden.nwsdata.R
import com.yaleiden.nwsdata.databinding.FragmentLakeLevelBinding

class LakeLevelFragment : Fragment() {

    private val tag: String = "LakeLevelFragment"
    private var _binding: FragmentLakeLevelBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(tag, "onCreateView ")
        val lakeLevelViewModel =
            ViewModelProvider(this).get(LakeLevelViewModel::class.java)
        Log.d(tag, "_binding ")
        _binding = FragmentLakeLevelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        lakeLevelViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val lakeLevelWebView = binding.lakeLevelWebView
        lakeLevelWebView.loadUrl("https://waterdata.usgs.gov/nwisweb/graph?agency_cd=USGS&site_no=02193900&parm_cd=00062&period=7")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: View = requireView().findViewById(R.id.fab_btn)
        fab.setOnClickListener {
            onClickRefresh()
        }
    }

    private fun onClickRefresh() {
        val lakeLevelWebView = binding.lakeLevelWebView
        lakeLevelWebView.loadUrl("https://waterdata.usgs.gov/nwisweb/graph?agency_cd=USGS&site_no=02193900&parm_cd=00062&period=7")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}