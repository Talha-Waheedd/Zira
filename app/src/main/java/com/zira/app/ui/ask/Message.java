package com.zira.app.ui.ask;

import java.util.List;

/** UI model for a single chat row in the Ask Zira conversation. */
public class Message {

    public static final int TYPE_USER = 0;
    public static final int TYPE_ZIRA = 1;

    private final int type;
    private final String text;
    private final List<String> followUps;

    /** When {@code true}, a Zira message reveals its text with a typewriter animation once. */
    private boolean animate;

    private Message(int type, String text, List<String> followUps, boolean animate) {
        this.type = type;
        this.text = text;
        this.followUps = followUps;
        this.animate = animate;
    }

    public static Message user(String text) {
        return new Message(TYPE_USER, text, null, false);
    }

    public static Message zira(String text, List<String> followUps, boolean animate) {
        return new Message(TYPE_ZIRA, text, followUps, animate);
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public List<String> getFollowUps() {
        return followUps;
    }

    public boolean shouldAnimate() {
        return animate;
    }

    public void markAnimated() {
        this.animate = false;
    }
}
