package uz.algo.near.models;

public class TypingCommand {

    private Long from;
    private boolean typing;

    public TypingCommand() {
    }

    public TypingCommand(Long from, boolean typing) {
        this.from = from;
        this.typing = typing;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
