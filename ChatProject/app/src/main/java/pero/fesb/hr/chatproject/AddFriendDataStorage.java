package pero.fesb.hr.chatproject;


import java.util.HashMap;

public class AddFriendDataStorage {
    public static HashMap<Integer, AddFriend> addFriendsListViewData = new HashMap<Integer, AddFriend>();

    public static AddFriend[] addFriends;


    public static void fillData() {
        addFriendsListViewData.clear();
        for(int i = 0; i < addFriends.length; i++){
            AddFriend aAddFriend = new AddFriend(i + 1, addFriends[i].getThumbnail(), addFriends[i].getName(), addFriends[i].getFriendsStatus());
            addFriendsListViewData.put(i, aAddFriend);
        }
    }
}
