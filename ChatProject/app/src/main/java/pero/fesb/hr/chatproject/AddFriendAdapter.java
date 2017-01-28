package pero.fesb.hr.chatproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.ion.Ion;

public class AddFriendAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public AddFriendAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return AddFriendDataStorage.addFriendsListViewData.size();
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
        if(view == null) view = mInflater.inflate(R.layout.each_add_friend, viewGroup, false);

        final ImageView addFriendThumbnail = (ImageView) view.findViewById(R.id.addFriendThumbnail);
        final TextView addFriendName = (TextView) view.findViewById(R.id.addFriendName);
        final ImageView addFriendStatus = (ImageView) view.findViewById(R.id.addFriendStatus);

        final AddFriend addFriend = AddFriendDataStorage.addFriendsListViewData.get(position);

        if(addFriend.getThumbnail().equals("")){
            Ion.with(addFriendThumbnail)
                    .placeholder(R.drawable.chat_icon_blue)
                    .error(R.drawable.chat_icon_orange)
                    .load(MainActivity.API_URL+"profile");
        }
        else {
            Ion.with(addFriendThumbnail)
                    .resize(80, 80)
                    .placeholder(R.drawable.chat_icon_blue)
                    .error(R.drawable.chat_icon_orange)
                    .load(addFriend.getThumbnail());
        }

        addFriendName.setText(addFriend.getName());

        if(addFriend.getFriendsStatus()) {
            addFriendStatus.setImageResource(R.drawable.tick);
        }
        else {
            addFriendStatus.setImageResource(R.drawable.plus);
        }

        return view;
    }
}
