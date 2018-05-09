package vipul.in.mychat.ModalClasses;

/**
 * Created by vipul on 23/1/18.
 */

public class SingleChat {

    private boolean seen;
    private long timestamp;
    private String chatWith,key,lastMessage;

    public SingleChat() {
        super();
    }

    public SingleChat(boolean seen, long timestamp,String chatWith,String key,String lastMessage) {
        this.seen = seen;
        this.key = key;
        this.timestamp = timestamp;
        this.chatWith = chatWith;
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setChatWith(String name) {

        this.chatWith = name;

    }

    public String getChatWith() {

        return chatWith;

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
