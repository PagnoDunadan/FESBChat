package pero.fesb.hr.chatproject;


import java.util.HashMap;

public class FriendMessagesDataStorage {
    public static HashMap<Integer, FriendMessage> friendMessagesListViewData = new HashMap<Integer, FriendMessage>();

    public static FriendMessage[] friendMessages;


    public static void fillData() {
        friendMessagesListViewData.clear();
        for(int i = 0; i < friendMessages.length; i++){
            FriendMessage aFriendMessage = new FriendMessage(i + 1, friendMessages[i].getSender(), friendMessages[i].getProfileImage(), friendMessages[i].getTime(), friendMessages[i].getMessage(), friendMessages[i].getSeen(), friendMessages[i].getDetailsVisible());
            friendMessagesListViewData.put(i, aFriendMessage);
        }
    }
}
