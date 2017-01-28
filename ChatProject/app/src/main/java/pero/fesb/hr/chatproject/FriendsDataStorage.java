package pero.fesb.hr.chatproject;


import java.util.HashMap;

public class FriendsDataStorage {
    public static HashMap<Integer, Friend> friendsListViewData = new HashMap<Integer, Friend>();

    public static Friend[] friends;


    public static void fillData() {
        //friendsListViewData = new HashMap<Integer, Friend>();
        friendsListViewData.clear();
        for(int i = 0; i < friends.length; i++){
            Friend aFriend = new Friend(i + 1, friends[i].getThumbnail(), friends[i].getName(), friends[i].getLastOnline());
            friendsListViewData.put(i, aFriend);
        }
    }
}
