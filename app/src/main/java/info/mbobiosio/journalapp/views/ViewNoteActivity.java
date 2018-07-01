package info.mbobiosio.journalapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.mbobiosio.journalapp.R;
import info.mbobiosio.journalapp.model.NotesModel;
import info.mbobiosio.journalapp.utils.Constants;

import static info.mbobiosio.journalapp.utils.Constants.convertToReadableTime;

public class ViewNoteActivity extends BaseActivity {

    @BindView(R.id.note_title)
    TextView mTitle;
    @BindView(R.id.note_date)
    TextView mDate;
    @BindView(R.id.note_data)
    TextView mNoteData;
    @BindView(R.id.note_category)
    TextView mCategory;

    private String mData;
    private DatabaseReference mDatabase, mJournal;
    private FirebaseAuth mAuth;
    private String mUid;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        initUI();

        initDB();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mData = bundle.getString("data");
            mJournal.child(mData).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NotesModel NotesModel = dataSnapshot.getValue(NotesModel.class);
                    mTitle.setText(NotesModel.getTitle());
                    mNoteData.setText(NotesModel.getNote());
                    mCategory.setText(NotesModel.getCategory());
                    mDate.setText(convertToReadableTime(NotesModel.getTime()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void initDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            mUid = firebaseUser.getUid();

            mJournal = mDatabase.child(Constants.JOURNAL_APP).child(mUid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_note:
                editNote();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editNote() {
        Intent edit_note = new Intent(getApplicationContext(), NoteEditActivity.class);
        edit_note.putExtra("key", mData);
        edit_note.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(edit_note);
        finish();
    }
}