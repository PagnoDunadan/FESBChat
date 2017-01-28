package pero.fesb.hr.chatproject;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public FriendsAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return FriendsDataStorage.friendsListViewData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null) view = mInflater.inflate(R.layout.each_friend, viewGroup, false);

        final ImageView friendThumbnail = (ImageView) view.findViewById(R.id.friendThumbnail);
        final TextView friendName = (TextView) view.findViewById(R.id.friendName);
        final TextView friendStatus = (TextView) view.findViewById(R.id.friendStatus);

        final Friend friend = FriendsDataStorage.friendsListViewData.get(position);

        if(friend.getThumbnail().equals("")){
            Ion.with(friendThumbnail)
                    .placeholder(R.drawable.chat_icon_blue)
                    .error(R.drawable.chat_icon_orange)
                    .load(MainActivity.API_URL+"profile");
        }
        else {
            Ion.with(friendThumbnail)
                    .resize(80, 80)
                    .placeholder(R.drawable.chat_icon_blue)
                    .error(R.drawable.chat_icon_orange)
                    .load(friend.getThumbnail());
        }

        friendName.setText(friend.getName());


        Date now = new Date();
        long difference = now.getTime() - friend.getLastOnline();

        if(difference >= 0 && difference < 60000) {
            friendStatus.setTextColor(Color.parseColor("#00C853"));
            friendStatus.setText("\u2022 Online");
        } else if(difference >= 60000 && difference < 3600000) {
            friendStatus.setTextColor(Color.parseColor("#000000"));
            friendStatus.setText( (int) difference/60000 + " minutes ago");
        } else if(difference >= 3600000 && difference < 86400000) {
            friendStatus.setTextColor(Color.parseColor("#000000"));
            friendStatus.setText( (int) difference/3600000 + " hours ago");
        } else {
            friendStatus.setTextColor(Color.parseColor("#000000"));
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            friendStatus.setText(formatter.format(friend.getLastOnline()));
        }

        return view;
    }
}
