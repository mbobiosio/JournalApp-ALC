package info.mbobiosio.journalapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.mbobiosio.journalapp.R;
import info.mbobiosio.journalapp.adapters.NotesAdapter;
import info.mbobiosio.journalapp.model.NotesModel;
import info.mbobiosio.journalapp.utils.Constants;
import info.mbobiosio.journalapp.utils.NetworkUtil;


public class MainActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUid;

    private List<NotesModel> mNotesModel;
    private List<String> mList;
    DatabaseReference mDatabase;
    DatabaseReference mDBRef;
    private NotesAdapter mNotesAdapter;
    @BindView(R.id.journal_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.create_new)
    FloatingActionButton mCreateNew;

    SnapHelper mSnapHelper;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNotesModel = new ArrayList<>();
        mList = new ArrayList<>();
        Collections.reverse(mNotesModel);

        initView();
        initDB();

        mCreateNew.setOnClickListener(view -> doNewJournal());

        if (mUser != null) {
            mDBRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        mList.clear();
                        mNotesModel.clear();
                        for (DataSnapshot mSnapshot : dataSnapshot.getChildren()) {
                            mList.add(mSnapshot.getKey());
                            NotesModel NotesModel = mSnapshot.getValue(NotesModel.class);
                            mNotesModel.add(NotesModel);
                            mNotesAdapter.notifyDataSetChanged();
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        }

        mNotesAdapter = new NotesAdapter(mNotesModel, getApplicationContext(), mList);
        mRecyclerView.setAdapter(mNotesAdapter);

    }
    public void initView() {
        mSnapHelper = new LinearSnapHelper();
        mSnapHelper.attachToRecyclerView(mRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    public void initDB() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDBRef = mDatabase.child(Constants.JOURNAL_APP);
    }

    public void doNewJournal() {
        Intent createNew = new Intent(this, NewNoteActivity.class);
        startActivity(createNew);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.profile:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            if (mUser == null) {
                Intent login = new Intent(this, LogInActivity.class);
                startActivity(login);
                finish();
            } else {
                mUid = mUser.getUid();
                mNotesAdapter.notifyDataSetChanged();
            }

        } else {
            showSnackBar(getString(R.string.no_active_internet));
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgress();
    }
}
