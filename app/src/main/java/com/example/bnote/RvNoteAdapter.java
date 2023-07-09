package com.example.bnote;

import static com.example.bnote.NoteDB.KEY_ROWID;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RvNoteAdapter extends RecyclerView.Adapter<RvNoteAdapter.ViewHolder> {

    private static final String TAG = "RvNoteAdapter";
    private ArrayList<Note> notes = new ArrayList<>();
    private Context mContext;
    private String parentActivity;

    public RvNoteAdapter(Context mContext, String parentActivity) {
        this.mContext = mContext;
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setNote(notes.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteActivity.class);
                intent.putExtra(KEY_ROWID, notes.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout card;
        private ImageView ivIsPinned;
        private TextView tvTitle, tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            card = itemView.findViewById(R.id.llCard);
            ivIsPinned = itemView.findViewById(R.id.ivIsPinned);
        }

        public void setNote(Note note) {
            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getContent());
            tvDate.setText(note.getDate());

            GradientDrawable cardBackground = (GradientDrawable) card.getBackground();
            cardBackground.setColor(Color.parseColor(note.getColor()));

            if (note.getIsPinned() == 1) {
                ivIsPinned.setVisibility(View.VISIBLE);
            } else {
                ivIsPinned.setVisibility(View.GONE);
            }

        }
    }
}
