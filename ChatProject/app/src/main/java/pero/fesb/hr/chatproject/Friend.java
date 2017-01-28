package pero.fesb.hr.chatproject;


public class Friend {

    private int ID;
    private String thumbnail;
    private String name;
    private long lastOnline;

    public Friend(int ID, String thumbnail, String name, long lastOnline) {
        this.ID = ID;
        this.thumbnail = thumbnail;
        this.name = name;
        this.lastOnline = lastOnline;
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

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

}
