package com.mlab.sms.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mlab.sms.MsgReceiver
import com.mlab.sms.databinding.FragmentFirstBinding
import com.mlab.sms.model.Msg


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    //using activityViewModels so that i don;t create a new instance of viewModel
    private val viewModel by activityViewModels<MainViewModel>()

    private val binding get() = _binding!!

    companion object{
        private var lastFirstVisiblePosition = 0
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getting all previously stored sms
        viewModel.smsList.observe(viewLifecycleOwner){
            //building conversations with RecyclerView
            binding.convRecyclerView.adapter =
                ConversationsRecyclerViewAdapter(it,requireActivity().supportFragmentManager,this)
        }

        //looking incomplete, so..
        binding.fab.setOnClickListener {
            Snackbar.make(view, "Not Implemented", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //we have to call BroadcastReceiver from onResume(), not from onViewCreated()
    //so that it does not get called twice
    override fun onResume() {
        super.onResume()
        receiveMsg()
    }

    private fun receiveMsg() {
        val receiver = object : MsgReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                try{
                    //converting intent data to sms List
                    val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent).toList()
                    if(sms.isNotEmpty()){
                        val msg = Msg(
                            phone = sms[0].displayOriginatingAddress,
                            sms = sms[0].messageBody,
                            time = sms[0].timestampMillis,
                            type = 1
                        )
                        Log.d("TAG", "onReceive: sms $msg")
                        //saving newly received sms to view model
                        viewModel.saveMsg(msg)
                    }

                }
                // Exception can occur while getting data from intent
                catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        //we have to register the receiver to get SMS
        //i can do it with receiver in manifest & receiver class but this approach gives me ability to save data in viewModel
        //i have also done it with receiver in manifest & receiver class for showing toast while app is closed
        registerReceiver(
            requireActivity(),
            receiver,
            IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
            RECEIVER_EXPORTED
        )
    }
}