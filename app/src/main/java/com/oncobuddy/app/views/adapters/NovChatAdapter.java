package com.oncobuddy.app.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.NovChat;

import java.util.ArrayList;
import java.util.List;

public class NovChatAdapter extends RecyclerView.Adapter<NovChatAdapter.NovChatViewHolder>{

    private List<NovChat> novChatList = null;
    public NovChatAdapter(List<NovChat> novChatList) {
        this.novChatList = novChatList;
    }
    @Override
    public void onBindViewHolder(NovChatViewHolder holder, int position) {
        NovChat novChat = this.novChatList.get(position);
        // If the message is a received message.
        /*if(novChat.getMsgType().equals(novChat.getMsgType()))
        {
            // Show received message in left linearlayout.
            holder.rlIncomingMsg.setVisibility(LinearLayout.VISIBLE);
            holder.tvIncomingMsg.setText(novChat.getMsgContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rlOutgoingMsg.setVisibility(LinearLayout.GONE);
        }
        // If the message is a sent message.
        else if(novChat.MSG_TYPE_SENT.equals(novChat.getMsgType()))
        {
            // Show sent message in right linearlayout.
            holder.rlOutgoingMsg.setVisibility(LinearLayout.VISIBLE);
            holder.tvOutgoingMsg.setText(novChat.getMsgContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rlIncomingMsg.setVisibility(LinearLayout.GONE);
        }*/
    }
    @Override
    public NovChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chat_view_item, parent, false);
        return new NovChatViewHolder(view);
    }
    @Override
    public int getItemCount() {
        if(novChatList==null)
        {
            novChatList = new ArrayList<NovChat>();
        }
        return novChatList.size();
    }

    public class NovChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlIncomingMsg;
        RelativeLayout rlOutgoingMsg;
        TextView tvIncomingMsg;
        TextView tvOutgoingMsg;

        public NovChatViewHolder(View itemView) {
            super(itemView);
            if (itemView != null) {
                /*rlIncomingMsg = (RelativeLayout) itemView.findViewById(R.id.rlIncomingMsg);
                rlOutgoingMsg = (RelativeLayout) itemView.findViewById(R.id.rlOutgoingMsg);
                tvIncomingMsg = (TextView) itemView.findViewById(R.id.tvIncomingMsg);
                tvOutgoingMsg = (TextView) itemView.findViewById(R.id.tvOutgoingMsg);*/
            }
        }
    }
}
