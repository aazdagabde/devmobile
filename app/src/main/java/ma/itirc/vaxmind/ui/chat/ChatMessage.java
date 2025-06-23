package ma.itirc.vaxmind.ui.chat;

/** Modèle représentant un message dans la conversation */
public class ChatMessage {
    public final String  text;
    public final boolean fromUser;

    public ChatMessage(String text, boolean fromUser) {
        this.text     = text;
        this.fromUser = fromUser;
    }
}
