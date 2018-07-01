package info.mbobiosio.journalapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.mbobiosio.journalapp.R;
import info.mbobiosio.journalapp.model.NotesModel;
import info.mbobiosio.journalapp.utils.Constants;

public class NoteEditActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.title)
    TextInputEditText mTitle;
    @BindView(R.id.notes)
    TextInputEditText mNote;
    @BindView(R.id.categories)
    Spinner mSpinner;

    List<String> note_categories;
    private String mData;
    private DatabaseReference mDatabase;
    private DatabaseReference mJournal;
    private FirebaseAuth mAuth;
    private String mUid;
    private FirebaseUser mUser;

    private String mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

        initDB();
        initUI();

        mSpinner.setOnItemSelectedListener(this);

        note_categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.note_categories)));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, note_categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mData = bundle.getString("data");
            mJournal.child(mData).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NotesModel model = dataSnapshot.getValue(NotesModel.class);
                    mNote.setText(model.getNote());
                    mTitle.setText(model.getTitle());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    public void initDB() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mJournal = mDatabase.child(Constants.JOURNAL_APP).child(mUid);
    }

    public void doUpdate() {
        showProgress();
        String title = mTitle.getText().toString();
        String body = mNote.getText().toString();
        Map<String, Object> updateJournal = new HashMap<>();
        updateJournal.put("title", title);
        updateJournal.put("note", body);
        updateJournal.put("category", mNotes);
        updateJournal.put("time", ServerValue.TIMESTAMP);
        mJournal.child(mData).setValue(updateJournal)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successToast(getString(R.string.note_edit_successful));
                        startActivity(new Intent(this,
                                ViewNoteActivity.class));
                    } else {
                        errorToast(getString(R.string.note_edit_failed));
                    }
                    hideProgress();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_journal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                doUpdate();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mNotes = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
