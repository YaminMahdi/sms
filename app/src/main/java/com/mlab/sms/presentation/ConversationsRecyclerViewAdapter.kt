package com.mlab.sms.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mlab.sms.R
import com.mlab.sms.databinding.ItemConversationBinding
import com.mlab.sms.model.Msg
import com.mlab.sms.util.toDateTime


class ConversationsRecyclerViewAdapter(
    private val convList: List<Pair<String, List<Msg>>>,
    private val manager: FragmentManager,
    private val firstFragment: FirstFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolderAdmin(
        private val binding: ItemConversationBinding,
        private val contest: Context,
        private val manager: FragmentManager,
        private val firstFragment: FirstFragment
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(list: List<Pair<String,List<Msg>>>, position: Int) {
            binding.phoneNo.text = list[position].first
            binding.lastSms.text = list[position].second.last().sms
            binding.lastSmsTime.text = list[position].second.last().time.toDateTime()

            //opening conversation
            binding.conversation.setOnClickListener {
                manager
                    .beginTransaction()
                    .run {
                        addToBackStack("FoodListFragment")
                        hide(firstFragment)
                        add(R.id.convList, SecondFragment.newInstance(list[position].first))
                        commit()
                    }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolderAdmin(
            ItemConversationBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup, false), viewGroup.context, manager , firstFragment
        )

    override fun getItemCount() = convList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderAdmin).bindView(convList, position)
    }
}
