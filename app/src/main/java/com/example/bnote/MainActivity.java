package com.example.bnote;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNotes;
    private RvNoteAdapter adapter;
    private ImageView ivNoNotes, ivAddNoteMain, ivRemoveText;
    private TextView tvNoNotes;
    private EditText etSearch;
    private ArrayList<Note> allNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        rvHandler();

        ivAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etSearch.getText().toString().isEmpty()) {
                    ivRemoveText.setVisibility(View.VISIBLE);
                }
                searchForNote(s.toString());
            }
        });


        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (etSearch.hasFocus()) {
                    getCurrentFocus().clearFocus();
                }
                return false;
            }
        });


        ivRemoveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                if (etSearch.hasFocus()) {
                    getCurrentFocus().clearFocus();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                ivRemoveText.setVisibility(View.GONE);
            }
        });
    }

    private void rvHandler() {
        try {
            NoteDB db = new NoteDB(this);
            db.open();
            allNotes = db.getAllNotes();
            if (allNotes.isEmpty()) {
                ivNoNotes.setVisibility(View.VISIBLE);
                tvNoNotes.setVisibility(View.VISIBLE);
            } else {
                ivNoNotes.setVisibility(View.GONE);
                tvNoNotes.setVisibility(View.GONE);
                adapter.setNotes(allNotes);
            }
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        adapter = new RvNoteAdapter(this, "MainActivity");
        ivAddNoteMain = findViewById(R.id.ivAddNoteMain);
        rvNotes = findViewById(R.id.rvNotes);
        ivNoNotes = findViewById(R.id.ivNoNotes);
        tvNoNotes = findViewById(R.id.tvNoNotes);
        etSearch = findViewById(R.id.etSearch);
        ivRemoveText = findViewById(R.id.ivRemoveText);
        rvNotes.setAdapter(adapter);
        rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        rvHandler();
    }

    private void searchForNote(String keyword) {

        try {
            NoteDB db = new NoteDB(this);
            db.open();
            if (keyword.trim().isEmpty()) {
                rvHandler();
            } else {
                ArrayList<Note> temp = new ArrayList<>();
                for (Note note : allNotes) {
                    if (note.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            || note.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                        temp.add(note);
                    }
                }
                adapter.setNotes(temp);
            }
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}