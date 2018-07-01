package info.mbobiosio.journalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import es.dmoral.toasty.Toasty;
import info.mbobiosio.journalapp.R;
import info.mbobiosio.journalapp.model.NotesModel;
import info.mbobiosio.journalapp.utils.Constants;
import info.mbobiosio.journalapp.views.NoteEditActivity;
import info.mbobiosio.journalapp.views.ViewNoteActivity;

import static info.mbobiosio.journalapp.utils.Constants.convertToReadableTime;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    public List<NotesModel> notesModelList;
    public List<String> mainList;

    private DatabaseReference mDBref;
    private DatabaseReference mJournals;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context mContext;

    public NotesAdapter(List<NotesModel> notesModelList, Context context, List<String> mainList) {
        this.notesModelList = notesModelList;
        this.mContext = context;
        this.mainList = mainList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String title = notesModelList.get(position).getTitle();
        String category = notesModelList.get(position).getCategory();
        long date = notesModelList.get(position).getTime();
        holder.setTitle(title);
        holder.setDate(date);
        holder.setCategory(category);

        holder.mEdit.setOnClickListener(v -> {
            Intent edit = new Intent(mContext, NoteEditActivity.class);
            edit.putExtra("edit", mainList.get(position));
            mContext.startActivity(edit);

        });

        holder.mCard.setOnClickListener(v -> {
            Intent data = new Intent(mContext, ViewNoteActivity.class);
            data.putExtra("data", mainList.get(position));
            mContext.startActivity(data);
        });

        holder.mDelete.setOnClickListener(v -> {
            mDBref = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            mJournals = mDBref.child(Constants.JOURNAL_APP).child(user.getUid());
            mJournals.child(mainList.get(position)).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(mContext, mContext.getString(R.string.note_delete_successful_message)).show();

                } else {
                    Toasty.error(mContext, mContext.getString(R.string.note_delete_failed_message)).show();
                }
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notesModelList.size());
                notifyDataSetChanged();
            });


        });
    }

    @Override
    public int getItemCount() {
        return notesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDate;
        private TextView mCategory;
        private ImageButton mDelete;
        private ImageButton mEdit;
        private CardView mCard;


        public ViewHolder(View itemView) {
            super(itemView);

            mDelete = itemView.findViewById(R.id.delete);
            mEdit = itemView.findViewById(R.id.edit);
            mCard = itemView.findViewById(R.id.note_card);
        }

        public void setTitle(String title) {
            mTitle = itemView.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setDate(long date) {
            mDate = itemView.findViewById(R.id.date);
            mDate.setText(convertToReadableTime(date));
        }

        public void setCategory(String category) {
            mCategory = itemView.findViewById(R.id.category);
            mCategory.setText(category);
        }

    }

}
