package com.mlab.sms.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mlab.sms.databinding.ItemLeftSmsBinding
import com.mlab.sms.databinding.ItemRightSmsBinding
import com.mlab.sms.model.Msg
import com.mlab.sms.util.toDateTime

class SmsRecyclerViewAdapter(
    private val smsList: List<Msg>,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolderLeft(
        private val binding: ItemLeftSmsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<Msg>, position: Int) {
            binding.sms.text = list[position].sms
            binding.time.text = list[position].time.toDateTime()
        }
    }

    class ViewHolderRight(
        private val binding: ItemRightSmsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<Msg>, position: Int) {
            binding.sms.text = list[position].sms
            binding.time.text = list[position].time.toDateTime()
        }
    }
    //needed for binding different Views for sent and received sms
    override fun getItemViewType(position: Int): Int = smsList[position].type

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 1 - Received, 2 - Sent
        when(viewType){
            1 ->{ //item_left_sms for received sms (1)
                return ViewHolderLeft(
                    ItemLeftSmsBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup, false)
                )
            }
            else ->{ //item_right_sms for send sms (2)
                return ViewHolderRight(
                    ItemRightSmsBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup, false)
                )
            }
        }
    }

    override fun getItemCount() = smsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 1 - Received, 2 - Sent
        when(holder.itemViewType){
            1 ->{
                (holder as ViewHolderLeft).bindView(smsList, position)
            }
            else ->{
                (holder as ViewHolderRight).bindView(smsList, position)
            }
        }
    }
}
