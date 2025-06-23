package ma.itirc.vaxmind.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ma.itirc.vaxmind.R;

/**
 * Affiche les bulles envoyées / reçues dans le RecyclerView du Chatbot.
 * Nécessite : item_chat_sent.xml et item_chat_received.xml (déjà créés).
 */
public class ChatAdapter extends ListAdapter<ChatMessage, ChatAdapter.Holder> {

    public ChatAdapter() {
        super(DIFF);
    }

    /* ----------  DiffUtil pour mises à jour efficaces ---------- */
    private static final DiffUtil.ItemCallback<ChatMessage> DIFF =
            new DiffUtil.ItemCallback<>() {
                @Override public boolean areItemsTheSame(@NonNull ChatMessage a,@NonNull ChatMessage b) {
                    return a == b;   // même référence suffit ici
                }
                @Override public boolean areContentsTheSame(@NonNull ChatMessage a,@NonNull ChatMessage b) {
                    return a.text.equals(b.text) && a.fromUser == b.fromUser;
                }
            };

    /* ----------  Holder ---------- */
    static class Holder extends RecyclerView.ViewHolder {
        TextView tv;
        Holder(@NonNull View v) {
            super(v);
            tv = v.findViewById(R.id.tvMsg);
        }
    }

    @Override public int getItemViewType(int position) {
        return getItem(position).fromUser ? 0 : 1;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 0)
                ? R.layout.item_chat_sent
                : R.layout.item_chat_received;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        h.tv.setText(getItem(position).text);
    }
}
