package com.kidozh.discuzhub.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.kidozh.discuzhub.R;
import com.kidozh.discuzhub.activities.UserProfileActivity;
import com.kidozh.discuzhub.activities.bbsPrivateMessageDetailActivity;
import com.kidozh.discuzhub.entities.bbsInformation;
import com.kidozh.discuzhub.entities.forumUserBriefInfo;
import com.kidozh.discuzhub.utilities.bbsConstUtils;
import com.kidozh.discuzhub.utilities.bbsParseUtils;
import com.kidozh.discuzhub.utilities.URLUtils;
import com.kidozh.discuzhub.utilities.networkUtils;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class bbsPrivateMessageAdapter extends RecyclerView.Adapter<bbsPrivateMessageAdapter.ViewHolder> {
    private static final String TAG = bbsPrivateMessageAdapter.class.getSimpleName();
    private List<bbsParseUtils.privateMessage> privateMessageList;
    private Context context;

    bbsInformation curBBS;
    forumUserBriefInfo userBriefInfo;

    public bbsPrivateMessageAdapter(bbsInformation curBBS, forumUserBriefInfo userBriefInfo){
        this.curBBS = curBBS;
        this.userBriefInfo = userBriefInfo;
    }

    public void setPrivateMessageList(List<bbsParseUtils.privateMessage> privateMessageList) {
        this.privateMessageList = privateMessageList;
        notifyDataSetChanged();
    }

    public void addPrivateMessageList(List<bbsParseUtils.privateMessage> privateMessageList) {
        if(this.privateMessageList == null){
            this.privateMessageList = privateMessageList;
        }
        else {
            this.privateMessageList.addAll(privateMessageList);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message,parent,false));
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bbsParseUtils.privateMessage privateM = privateMessageList.get(position);
        holder.privateMessageContent.setText(privateM.message);
        Spanned timeSp = Html.fromHtml(privateM.vdateLine);
        if(privateM.isNew){
            Log.d(TAG,"THIS IS NEW PRIVATE MESSAGE "+privateM.message);

            BadgeDrawable badgeDrawable = BadgeDrawable.create(context);
            badgeDrawable.setNumber(1);
            badgeDrawable.setVisible(true);

            BadgeUtils.attachBadgeDrawable(badgeDrawable, holder.privateMessageAvatar, null);
            holder.privateMessageUsername.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        else {
            holder.privateMessageUsername.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }


        holder.privateMessageRecvTime.setText(new SpannableString(timeSp));

        //holder.privateMessageRecvTime.setText(privateM.vdateLine);
        holder.privateMessageUsername.setText(privateM.toUsername);
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(networkUtils.getPreferredClient(context));
        Glide.get(context).getRegistry().replace(GlideUrl.class, InputStream.class,factory);

        int avatar_num = position % 16;

        int avatarResource = context.getResources().getIdentifier(String.format("avatar_%s",avatar_num+1),"drawable",context.getPackageName());

        Glide.with(context)
                .load(URLUtils.getSmallAvatarUrlByUid(String.valueOf(privateM.toUid)))
                .centerInside()
                .placeholder(avatarResource)
                .error(avatarResource)
                .into(holder.privateMessageAvatar);
        holder.privateMessageCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, bbsPrivateMessageDetailActivity.class);
                intent.putExtra(bbsConstUtils.PASS_BBS_ENTITY_KEY,curBBS);
                intent.putExtra(bbsConstUtils.PASS_BBS_USER_KEY,userBriefInfo);
                intent.putExtra(bbsConstUtils.PASS_PRIVATE_MESSAGE_KEY,privateM);

                context.startActivity(intent);
            }
        });

        holder.privateMessageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(bbsConstUtils.PASS_BBS_ENTITY_KEY,curBBS);
                intent.putExtra(bbsConstUtils.PASS_BBS_USER_KEY,userBriefInfo);

                intent.putExtra("UID", String.valueOf(privateM.toUid));

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(privateMessageList == null){
            return 0;
        }
        else {
            return privateMessageList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_private_message_cardview)
        CardView privateMessageCardview;
        @BindView(R.id.item_private_message_avatar)
        ImageView privateMessageAvatar;
        @BindView(R.id.item_private_message_content)
        TextView privateMessageContent;
        @BindView(R.id.item_private_message_recv_time)
        TextView privateMessageRecvTime;
        @BindView(R.id.item_private_message_username)
        TextView privateMessageUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
