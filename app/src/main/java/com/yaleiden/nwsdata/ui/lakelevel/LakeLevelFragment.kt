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
import android.webkit.WebView
import android.webkit.WebViewClient
import android.graphics.Bitmap // Required for the favicon parameter



class LakeLevelFragment : Fragment() {

    private val tag: String = "LakeLevelFragment"
    private var _binding: FragmentLakeLevelBinding? = null
    private val binding get() = _binding!!

    private val levelUrl: String = "https://waterdata.usgs.gov/nwisweb/graph?agency_cd=USGS&site_no=02193900&parm_cd=00062&period=7"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLakeLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewModel
        val lakeLevelViewModel = ViewModelProvider(this).get(LakeLevelViewModel::class.java)
        lakeLevelViewModel.text.observe(viewLifecycleOwner) {
            binding.textNotifications.text = it
        }

        // Configure WebView
        setupWebView()

        // Setup FAB using View Binding
        // Assuming your XML ID is actually fab_btn
        binding.fabBtn.setOnClickListener {
            onClickRefresh()
        }
    }

    private fun setupWebView() {
        with(binding.lakeLevelWebView) {
            settings.apply {
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                javaScriptEnabled = true
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                    // Optional: keep it hidden until some content is ready
                    // view?.visibility = View.INVISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                    // Make it visible now that the graph is loaded
                    view?.visibility = View.VISIBLE
                }
            }

            // Show the bar MANUALLY before the first load
            binding.progressBar.visibility = View.VISIBLE
            loadUrl(levelUrl)
        }
    }

    private fun onClickRefresh() {
        Log.d(tag, "onClickRefresh: Reloading URL")
        // Show the bar MANUALLY before the reload
        binding.progressBar.visibility = View.VISIBLE
        binding.lakeLevelWebView.reload()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}