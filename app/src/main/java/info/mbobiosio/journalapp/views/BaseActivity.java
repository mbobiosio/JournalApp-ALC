package info.mbobiosio.journalapp.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import info.mbobiosio.journalapp.R;
import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgress;
    public Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showProgress() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getString(R.string.progress_message));
        mProgress.setIndeterminate(true);
        mProgress.show();
    }

    public void hideProgress() {
        mProgress.hide();
    }

    public void showSnackBar(String string) {
        Snackbar.make(findViewById(android.R.id.content), string, Snackbar.LENGTH_INDEFINITE).show();
    }

    public void errorToast(String string) {
        Toasty.error(this, string).show();
    }

    public void successToast(String string) {
        Toasty.success(this, string).show();
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
    public void initUI() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onNavigateUp());
    }
}
