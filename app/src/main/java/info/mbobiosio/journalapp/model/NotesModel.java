package info.mbobiosio.journalapp.model;



public class NotesModel {
    public String title;
    public String note;
    public String category;
    public long time;

    public NotesModel(String title, String note, String category, long time) {
        this.title = title;
        this.note = note;
        this.category = category;
        this.time = time;
    }

    public NotesModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
