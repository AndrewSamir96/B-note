package com.example.bnote;
import static com.example.bnote.NoteDB.KEY_ROWID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NoteActivity extends AppCompatActivity {

    private EditText etNoteTitle, etNoteContent;
    private TextView tvDateTime;
    private ImageView ivPin, ivBack, imageColor1, imageColor2, imageColor3, imageColor4, imageColor5, ivDelete;
    private int noteId , isPinned;
    private String selectedNoteColor;
    private View vSubtitleIndicator;
    private AlertDialog dialogDeleteNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initData();
        initActivity();
        initBottomBar();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPinned == 0){
                    isPinned = 1;
                    ivPin.setImageResource(R.drawable.ic_pin_filled);
                    Toast.makeText(NoteActivity.this, "Note pinned!", Toast.LENGTH_SHORT).show();

                } else if (isPinned == 1) {
                    isPinned = 0;
                    ivPin.setImageResource(R.drawable.ic_pin);
                    Toast.makeText(NoteActivity.this, "Note unpinned!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteNoteDialog();
            }
        });
    }


    //setting the activity for new note or from Database
    private void initActivity() {
        //if there's no notes just set the new date and default note colors
        tvDateTime.setText(new SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault()).format(new Date()));
        selectedNoteColor = "#333333";
        isPinned = 0;
        setSubtitleColor("#000000");

        //if there's a note
        Intent intent = getIntent();
        if (intent != null){
            noteId = intent.getIntExtra(KEY_ROWID,-1);
            if(noteId != -1){
                try {
                    NoteDB db = new NoteDB(this);
                    db.open();
                    Note incomingNote = db.getNote(noteId);
                    db.close();
                    if (incomingNote != null){
                        etNoteTitle.setText(incomingNote.getTitle());
                        etNoteContent.setText(incomingNote.getContent());
                        tvDateTime.setText(incomingNote.getDate());
                        changeNoteColor(incomingNote.getColor());
                        if (incomingNote.getIsPinned() == 1){
                            isPinned = 1;
                            ivPin.setImageResource(R.drawable.ic_pin_filled);
                        }
                    }
                }
                catch (SQLException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //initializing the activity contents
    private void initData() {
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        tvDateTime = findViewById(R.id.tvDateTime);
        ivPin = findViewById(R.id.ivPin);
        ivBack = findViewById(R.id.ivBack);
        vSubtitleIndicator = findViewById(R.id.vTitleIndicator);
        imageColor1 = findViewById(R.id.ivColor1);
        imageColor2 = findViewById(R.id.ivColor2);
        imageColor3 = findViewById(R.id.ivColor3);
        imageColor4 = findViewById(R.id.ivColor4);
        imageColor5 = findViewById(R.id.ivColor5);
        ivDelete = findViewById(R.id.ivDelete);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveNote();
    }

    private void saveNote() {
        if (noteId != -1){
            try {
                NoteDB db = new NoteDB(this);
                db.open();
                db.updateNote(noteId, etNoteTitle.getText().toString().trim(), etNoteContent.getText().toString().trim(), selectedNoteColor, tvDateTime.getText().toString().trim(),isPinned);
                db.close();
                Toast.makeText(this, "Note Updated!", Toast.LENGTH_SHORT).show();
            }
            catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            try {
                if (!etNoteTitle.getText().toString().trim().isEmpty() || !etNoteContent.getText().toString().trim().isEmpty()){
                    NoteDB db = new NoteDB(this);
                    db.open();
                    db.createNote(etNoteTitle.getText().toString().trim(), etNoteContent.getText().toString().trim(), selectedNoteColor, tvDateTime.getText().toString().trim(),isPinned);
                    db.close();
                    Toast.makeText(this, "New note Saved!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Note Discarded", Toast.LENGTH_SHORT).show();
                }

            }
            catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initBottomBar(){
        final LinearLayout bottomLayout = findViewById(R.id.bottomLayout);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomLayout);

        bottomLayout.findViewById(R.id.tvMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bottomLayout.findViewById(R.id.ivColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNoteColor("#333333");
            }
        });
        bottomLayout.findViewById(R.id.ivColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNoteColor("#77172E");
            }
        });
        bottomLayout.findViewById(R.id.ivColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNoteColor("#0C625D");
            }
        });
        bottomLayout.findViewById(R.id.ivColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNoteColor("#284255");
            }
        });
        bottomLayout.findViewById(R.id.ivColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNoteColor("#000000");
            }
        });
    }

    private void changeNoteColor(String selectedColor) {

        selectedNoteColor = selectedColor;
        if (selectedColor.equals("#333333")){
            imageColor1.setImageResource(R.drawable.icc_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleColor("#000000");

            CoordinatorLayout clNoteLayout = findViewById(R.id.clNoteLayout);
            clNoteLayout.setBackground(getDrawable(R.color.colorSearchBackground));
        } else if (selectedColor.equals("#77172E")) {
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.icc_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleColor("#000000");

            CoordinatorLayout clNoteLayout = findViewById(R.id.clNoteLayout);
            clNoteLayout.setBackground(getDrawable(R.color.colorNoteColor2));
        } else if (selectedColor.equals("#0C625D")) {
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.icc_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleColor("#000000");

            CoordinatorLayout clNoteLayout = findViewById(R.id.clNoteLayout);
            clNoteLayout.setBackground(getDrawable(R.color.colorNoteColor3));
        } else if (selectedColor.equals("#284255")) {
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.icc_done);
            imageColor5.setImageResource(0);
            setSubtitleColor("#000000");

            CoordinatorLayout clNoteLayout = findViewById(R.id.clNoteLayout);
            clNoteLayout.setBackground(getDrawable(R.color.colorNoteColor4));
        } else if (selectedColor.equals("#000000")) {
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.icc_done);
            setSubtitleColor("#333333");

            CoordinatorLayout clNoteLayout = findViewById(R.id.clNoteLayout);
            clNoteLayout.setBackground(getDrawable(R.color.colorNoteColor5));
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(selectedNoteColor));
    }

    private void setSubtitleColor(String selectedNoteColors){
        GradientDrawable gradientDrawable = (GradientDrawable)vSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColors));
    }

    private void showDeleteNoteDialog(){
        if (dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.delete_note_layout,
                    (ViewGroup) findViewById(R.id.deleteNoteLayout)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.tvDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        NoteDB db = new NoteDB(NoteActivity.this);
                        db.open();
                        db.deleteNote(noteId);
                        db.close();
                        Toast.makeText(NoteActivity.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    catch (SQLException e) {
                        Toast.makeText(NoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            view.findViewById(R.id.tvCancelNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }
}