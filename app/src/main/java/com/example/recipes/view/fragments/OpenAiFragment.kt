package com.example.recipes.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recipes.R
import com.example.recipes.databinding.FragmentOpenAiBinding
import com.example.recipes.model.network.ChatGPTResponse
import com.example.recipes.model.network.OpenAIRequest
import com.example.recipes.model.network.OpenAiAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OpenAiFragment : Fragment(R.layout.fragment_open_ai) {
    private val TAG = "OpenAiFragment"
    private var openAiBinding : FragmentOpenAiBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        openAiBinding = FragmentOpenAiBinding.inflate(inflater, container, false)
        return openAiBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openAiBinding?.ivSendQuery?.setOnClickListener{
            val query = openAiBinding?.etSearchQueryForOpenAi?.text.toString()

            getResponse(query)
        }

    }

    private fun getResponse(query: String){
        Log.d(TAG, "getResponse: Here")
        val apiService = OpenAiAPIService()
        lifecycleScope.launch {
            try {
                val response = apiService.getResponse(
                    OpenAIRequest(
                        model = "text-davinci-003",
                        query,
                        max_tokens = 7,
                        temperature = 0.0
                    )
                )

                Log.i(TAG, "getResponse: ${response.choices[0].text}")
            } catch (e: Exception) {
                // Handle the error here
                Log.i(TAG, "getResponse: ${e.message}")
            }
        }
    }
}