package com.zamanak.testone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zamanak.testone.objects.Note;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NoteDataTypeActivity extends AppCompatActivity {

    /**
     * basic observable , observer , subscriber example
     * introduced CompositeDisposable and DisposableObserver
     * The observable emits custom data type (Note) instead of primitive data tpes
     * ---
     * map() operator is used to turn the note into all uppercase letters
     * ---
     * you can also notice we got rid of the below declarations
     * observable<Note> notesObservable = getNotesObservable();
     * DisposableObserver<Note> notesObserver = getNotesObserver();
     */

    private static final String TAG = NoteDataTypeActivity.class.getSimpleName();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_data_type);

        // add to Composite observable
        // map() operator is used to turn the note into all uppercase letters

        compositeDisposable.add(getNoteObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Function<Note, Note>() {
            @Override
            public Note apply(Note note) throws Exception {
                // making the note to all uppercase
                note.setNote(note.getNote().toUpperCase());
                return note;
            }
        }).subscribeWith(getNotesObserver()));

    }

    // this method create a list of notes
    private ArrayList<Note> prepareNoteList() {
        ArrayList<Note> noteList = new ArrayList<Note>();
        noteList.add(new Note(1, "buy tooth paste"));
        noteList.add(new Note(2, "call brother"));
        noteList.add(new Note(3, "watch narcos tonight"));
        noteList.add(new Note(4, "pay power bill!"));
        return noteList;
    }

    // this method should emits the notes list
    private Observable<Note> getNoteObservable() {

        final ArrayList<Note> noteList = prepareNoteList();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note : noteList) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }
                if (emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private DisposableObserver<Note> getNotesObserver(){
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note note) {
                Log.d(TAG, "onNext: "+note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: "+e.getMessage() );
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: "+"All notes are emitted");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
