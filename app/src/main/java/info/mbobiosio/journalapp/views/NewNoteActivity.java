package info.mbobiosio.journalapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.mbobiosio.journalapp.R;
import info.mbobiosio.journalapp.utils.Constants;

public class NewNoteActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.title)
    TextInputEditText mTitle;
    @BindView(R.id.notes)
    TextInputEditText mNote;
    @BindView(R.id.categories)
    Spinner mSpinner;

    private DatabaseReference mDatabase;
    private DatabaseReference mNewJournal;
    private FirebaseAuth mAuth;
    ArrayAdapter<String> mAdapter;
    private String mUid;
    private String item;

    List<String> note_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_activity);
        ButterKnife.bind(this);

        initDB();
        initUI();

        mSpinner.setOnItemSelectedListener(this);

        note_categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.note_categories)));
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, note_categories);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);

    }

    public void createNew() {
        if (mTitle.getText().toString().isEmpty()) {
            errorToast(getString(R.string.new_note_title_empty));
            return;
        }
        if (mNote.getText().toString().isEmpty()) {
            errorToast(getString(R.string.new_note_empty));
            return;
        }

        showProgress();
        String title = mTitle.getText().toString();
        String body = mNote.getText().toString();
        Map<String, Object> mPost = new HashMap<>();
        mPost.put("title", title);
        mPost.put("category", item);
        mPost.put("note", body);
        mPost.put("time", ServerValue.TIMESTAMP);
        mNewJournal.child(mUid).push().setValue(mPost)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successToast(getString(R.string.new_note_added_message));

                        Intent done = new Intent(NewNoteActivity.this, MainActivity.class);
                        startActivity(done);

                    } else if (task.isCanceled()) {

                        successToast(getString(R.string.new_note_cancelled));

                    } else {

                        errorToast(getString(R.string.oops_error));

                    }
                    hideProgress();
                });

    }

    public void initDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNewJournal = mDatabase.child(Constants.JOURNAL_APP);
        mAuth = FirebaseAuth.getInstance();
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
                createNew();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mUid = mAuth.getCurrentUser().getUid();
        } else {
            startActivity(new Intent(this, LogInActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
