package com.mlab.sms.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mlab.sms.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var senderPhone : String

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //getting sender phone number of a conversation
        arguments?.let {
            senderPhone = it.getString("sender")?: ""
        }
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.smsList.observe(viewLifecycleOwner){lst->

            //getting conversation of that phone number
            val ind = lst.map { it.first }.indexOf(senderPhone)
            val convData = lst[ind].second

            binding.nm.text = senderPhone  //phone number at top
            binding.smsRecyclerView.adapter = SmsRecyclerViewAdapter(convData) //giving sms list to adapter
            binding.smsRecyclerView.scrollToPosition(convData.size-1) //scrolling to bottom
        }
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() //going back to main page
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(sender: String) =
            SecondFragment().apply {
                //getting sender phone number of a conversation
                arguments = bundleOf("sender" to sender)
            }
    }
}