package com.zamanak.testone.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.zamanak.testone.R;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatTextView tvCustomDataType, tvOperators;
    private static final String TAG = "Observer";

    /**
     * Disposable is used to dispose the subscription when an Observer no longer wants to listen to Observable
     * if the activity / fragment is already destroyed, as the Observer subscription is still alive, it tries to update already destroyed activity. In this case it can throw a memory leak. So using the Disposables, the un-subscription can be when the activity is destroyed.
     */
    private Disposable disposable;

    /**
     * Can maintain list of subscriptions in a pool and can dispose them all at once.Usually we call compositeDisposable.clear() in onDestroy() method, but you can call anywhere in the code
     */
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFindViewByIds();
        setOnClickListener();

        // 1- create observable
        Observable<String> animalObservable = getAnimalObservable();

        // 2- Create an Observer that listen to Observable
        /**
         * onSubscribe(): Method will be called when an Observer subscribes to Observable.
         * onNext(): This method will be called when Observable starts emitting the data.
         * onError(): In case of any error, onError() method will be called.
         * onComplete(): When an Observable completes the emission of all the items, onComplete() will be called.
         */
        //Observer<String> animalObserver = getAnimalObserver();
        DisposableObserver<String> animalDispoObserver = getAnimalDispoObserver(); // filter animals that start with letter 'b'
        DisposableObserver<String> animalAllCapsDispoObserver = getAnimalsAllCapsDispoObserver(); // just convert all animals name to upper case

        // 3-  Make Observer subscribe to Observable so that it can start receiving the data.
        /**
         * notice two more methods, observeOn() and subscribeOn().
         * subscribeOn(Schedulers.io()): This tell the Observable to run the task on a background thread.
         * observeOn(AndroidSchedulers.mainThread()): This tells the Observer to receive the data on android UI thread so that you can take any UI related actions.
         */
        // observer subscribing to observable - > method subscribe() just emitted the observable
        /*animalObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(animalObserver);*/

        // user filter() operator to filter animal names which starts with letter 'b' will be filtered
        /*animalObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.toLowerCase().startsWith("b");
                    }
                })
                .subscribeWith(animalObserver);*/

        /**
         * filter() is used to filter out the animal names starting with 'b'
         */
        compositeDisposable.add(animalObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s.toLowerCase().startsWith("b");
            }
        }).subscribeWith(animalDispoObserver));

        /**
         * filter() is used to filter out the animal names starting with 'c'
         * map() is used to transform all the character to uppercase
         */
        compositeDisposable.add(animalObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s.toLowerCase().startsWith("c");
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s.toUpperCase();
            }
        }).subscribeWith(animalAllCapsDispoObserver));


    }


    //observer for one observable
    private Observer<String> getAnimalObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted");
            }
        };
    }

    //observer for handle multiple observable
    private DisposableObserver<String> getAnimalDispoObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: " + "All items are emitted");
            }
        };
    }

    private DisposableObserver<String> getAnimalsAllCapsDispoObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: " + "All items are emitted");
            }
        };
    }

    private Observable<String> getAnimalObservable() {
        // Here just() operator is used to emit few animal names.
        //return Observable.just("Ant", "Bee", "Cat", "Dog", "Fox");
        return Observable.fromArray("Ant", "Ape", "Bat", "Bee", "Bear", "ButterFly", "Cat", "Crab", "Cod", "Dog", "Dove", "Fox", "Frog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send events once the activity is destroyed
        //disposable.dispose();

        compositeDisposable.clear();
    }


    private void setFindViewByIds() {
        tvCustomDataType = findViewById(R.id.tv_custom_data_type);
        tvOperators = findViewById(R.id.tv_operators);
    }

    private void setOnClickListener() {

        tvCustomDataType.setOnClickListener(this);
        tvOperators.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == tvCustomDataType.getId()) {
            startActivity(new Intent(this, NoteDataTypeActivity.class));
        }else if (v.getId()==tvOperators.getId()){
            startActivity(new Intent(this, OperatorsActivity.class));
        }
    }
}
