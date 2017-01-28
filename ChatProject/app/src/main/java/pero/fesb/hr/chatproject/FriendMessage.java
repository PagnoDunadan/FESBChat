package pero.fesb.hr.chatproject;


public class FriendMessage {
    private int ID;
    private String sender;
    private String profileImage;
    private long time;
    private String message;
    private String seen;
    private boolean detailsVisible = false;

    public FriendMessage(int ID, String sender, String profileImage, long time, String message, String seen, boolean detailsVisible) {
        this.ID = ID;
        this.sender = sender;
        this.profileImage = profileImage;
        this.time = time;
        this.message = message;
        this.seen = seen;
        this.detailsVisible = detailsVisible;
    }


    public int getID() {
        return ID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public boolean getDetailsVisible() {
        return detailsVisible;
    }

    public void setDetailsVisible(boolean detailsVisible) {
        this.detailsVisible = detailsVisible;
    }
}
