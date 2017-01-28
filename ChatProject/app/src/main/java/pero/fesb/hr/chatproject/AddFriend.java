package pero.fesb.hr.chatproject;


public class AddFriend {

    private int ID;
    private String thumbnail;
    private String name;
    private boolean friendsStatus;

    public AddFriend(int ID, String thumbnail, String name, boolean friendsStatus) {
        this.ID = ID;
        this.thumbnail = thumbnail;
        this.name = name;
        this.friendsStatus = friendsStatus;
    }


    public int getID() {
        return ID;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getFriendsStatus() {
        return friendsStatus;
    }

    public void setLastOnline(boolean friendsStatus) {
        this.friendsStatus = friendsStatus;
    }

}
