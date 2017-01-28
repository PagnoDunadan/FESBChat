package pero.fesb.hr.chatproject;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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


public class FriendMessagesAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public FriendMessagesAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return FriendMessagesDataStorage.friendMessagesListViewData.size();
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
        if(view == null) view = mInflater.inflate(R.layout.each_friend_message, viewGroup, false);

        final ImageView friendMessageThumbnail = (ImageView) view.findViewById(R.id.friendMessageThumbnail);
        final ImageView userMessageThumbnail = (ImageView) view.findViewById(R.id.userMessageThumbnail);
        final TextView friendMessage = (TextView) view.findViewById(R.id.friendMessage);
        final TextView friendMessageTime = (TextView) view.findViewById(R.id.friendMessageTime);
        final TextView friendMessageSeen = (TextView) view.findViewById(R.id.friendMessageSeen);

        final MyPreferences myPreferences = new MyPreferences(mContext);

        final FriendMessage message = FriendMessagesDataStorage.friendMessagesListViewData.get(position);


        if((message.getSender()).equals(myPreferences.getString("user"))){

            friendMessageThumbnail.setImageDrawable(null);

            if(message.getProfileImage().equals("")){
                Ion.with(userMessageThumbnail)
                        .placeholder(R.drawable.chat_icon_blue)
                        .error(R.drawable.chat_icon_orange)
                        .load(MainActivity.API_URL+"profile");
            }
            else {
                Ion.with(userMessageThumbnail)
                        .resize(80, 80)
                        .placeholder(R.drawable.chat_icon_blue)
                        .error(R.drawable.chat_icon_orange)
                        .load(message.getProfileImage());
            }

            Date date = new Date(message.getTime());
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            friendMessageTime.setText(formatter.format(date));
            friendMessageTime.setGravity(Gravity.RIGHT);

            friendMessage.setText(message.getMessage());
            friendMessage.setBackgroundColor(Color.parseColor("#00E676"));

            if(!message.getSeen().equals("||")){
                String seen = message.getSeen().substring(2, message.getSeen().length() - 2);
                seen = seen.replace("||", ", ");
                friendMessageSeen.setText("Seen by " + seen);
                friendMessageSeen.setGravity(Gravity.RIGHT);
            } else {
                friendMessageSeen.setText("");
            }
        }
        else {

            userMessageThumbnail.setImageDrawable(null);

            if(message.getProfileImage().equals("")){
                Ion.with(friendMessageThumbnail)
                        .placeholder(R.drawable.chat_icon_blue)
                        .error(R.drawable.chat_icon_orange)
                        .load(MainActivity.API_URL+"profile");
            }
            else {
                Ion.with(friendMessageThumbnail)
                        .resize(80, 80)
                        .placeholder(R.drawable.chat_icon_blue)
                        .error(R.drawable.chat_icon_orange)
                        .load(message.getProfileImage());
            }

            Date date = new Date(message.getTime());
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            friendMessageTime.setText(formatter.format(date));
            friendMessageTime.setGravity(Gravity.LEFT);

            friendMessage.setText(message.getMessage());
            friendMessage.setBackgroundColor(Color.parseColor("#FFFFFF"));

            if(!message.getSeen().equals("||")){
                String seen = message.getSeen().substring(2, message.getSeen().length() - 2);
                seen = seen.replace("||", ", ");
                friendMessageSeen.setText("Seen by " + seen);
                friendMessageSeen.setGravity(Gravity.LEFT);
            } else {
                friendMessageSeen.setText("");
            }
        }

        if(message.getDetailsVisible()){
            friendMessageTime.setVisibility(View.VISIBLE);
            friendMessageSeen.setVisibility(View.VISIBLE);
        } else {
            friendMessageTime.setVisibility(View.GONE);
            friendMessageSeen.setVisibility(View.GONE);
        }

        return view;
    }
}
