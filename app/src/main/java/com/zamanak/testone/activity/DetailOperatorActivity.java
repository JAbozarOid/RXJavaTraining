package com.zamanak.testone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.zamanak.testone.R;
import com.zamanak.testone.objects.OperatorsObj;
import com.zamanak.testone.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailOperatorActivity extends Activity {

    @BindView(R.id.tv_topic)
    AppCompatTextView tvTopic;

    @BindView(R.id.layout_tap_area)
    Button layoutTapArea;
    @BindView(R.id.tap_result)
    TextView tapResult;
    @BindView(R.id.tap_result_max_count)
    TextView tapResultMaxCount;
    @BindView(R.id.ll_buffer)
    LinearLayout llBuffer;

    @BindView(R.id.tv_search_string)
    TextView tvSearchString;
    @BindView(R.id.edt_input_search)
    AppCompatEditText edtInputSearch;
    @BindView(R.id.ll_debounce)
    LinearLayout llDebounce;

    private Unbinder unbinder;

    private OperatorsObj mOPeratorObj;
    private ArrayList<Integer> numbers;
    private Observable<User> userObservable;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private int maxTaps = 0;

    private final static String JUSTTAG1 = "JustOperator1";
    private final static String JUSTTAG2 = "JustOperator2";
    private final static String FROMTAG = "FromOperator";
    private final static String BUFFERTAG = "BufferOperator";
    private final static String DEBOUNCETAG = "DebounceOperator";
    private final static String FILTERTAG = "FilterOperator";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_operator);
        unbinder = ButterKnife.bind(this);

        getBundle();
        setTvTitle();
        createIntNums();


        switch (mOPeratorObj.getmPosition()) {
            case 1:
                prepareJustOperator1();
                prepareJustOperator2();
                break;
            case 2:
                prepareFromOperator();
                break;
            case 4:
                llBuffer.setVisibility(View.VISIBLE);
                prepareBufferOperator();
                break;
            case 5:
                llDebounce.setVisibility(View.VISIBLE);
                tvSearchString.setText("Search query will be accumulated every 300 milli sec");
                compositeDisposable = new CompositeDisposable();
                prepareDebounceOperator();
                break;
            case 6:
                // in this sample code use of filter() operator we want to show just the users who are female
                // in this sample we want just to filter the female gender
                userObservable = getUserObservable();
                prepareFilterOperator();
                break;
        }


    }

    private Observable<User> getUserObservable() {

        String[] maleNames = new String[]{"Mohsen","Abozar","Mohamad","Hale Hale"};
        String[] femaleNames = new String[]{"Nicole","Angelina","Emily","Megan"};

        final ArrayList<User> userList = new ArrayList<User>();

        for (String s : maleNames){
            User userObj = new User();
            userObj.setName(s);
            userObj.setGender("male");
            userList.add(userObj);
        }

        for (String s : femaleNames){
            User userObj = new User();
            userObj.setName(s);
            userObj.setGender("female");
            userList.add(userObj);
        }

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                for (User user : userList){
                    if (!emitter.isDisposed()){
                        emitter.onNext(user);
                    }
                }
                if (emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    private void prepareFilterOperator() {
        userObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<User>() {
                          @Override
                          public boolean test(User user) throws Exception {
                              return user.getGender().equalsIgnoreCase("female");
                          }
                      }).subscribeWith(new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                Log.d(FILTERTAG, "onNext: "+user.getName() + ", " + user.getGender());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void prepareDebounceOperator() {
        compositeDisposable.add(
                RxTextView.textChangeEvents(edtInputSearch)
                        .skipInitialValue()
                        .debounce(2000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(searchQuery()));
    }

    private DisposableObserver<TextViewTextChangeEvent> searchQuery() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                Log.d(DEBOUNCETAG, "Search String" + textViewTextChangeEvent.text().toString());
                tvSearchString.setText("Query: " + textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void prepareBufferOperator() {
        RxView.clicks(layoutTapArea).map(new Function<Object, Integer>() {
            @Override
            public Integer apply(Object o) throws Exception {
                return 1;
            }
        }).buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.e(BUFFERTAG, "onNext: " + integers.size() + "taps received!");
                        if (integers.size() > 0) {
                            maxTaps = integers.size() > maxTaps ? integers.size() : maxTaps;
                            tapResult.setText(String.format("Received %d taps in 3 secs", integers.size()));
                            tapResultMaxCount.setText(String.format("Maximum of %d taps received in this session", maxTaps));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(BUFFERTAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(BUFFERTAG, "onComplete: ");
                    }
                });
    }

    private void prepareFromOperator() {
        Observable.fromArray(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Integer> integers) {
                        Log.d(FROMTAG, "onNext: " + integers);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void prepareJustOperator2() {
        //int[] numbers = new int[]{1,2,3,4,5,6,7,8,9,10};
        Observable.just(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Integer> integers) {
                        Log.d(JUSTTAG2, "onNext: " + integers.toString());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void prepareJustOperator1() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(JUSTTAG1, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getBundle() {
        mOPeratorObj = (OperatorsObj) this.getIntent().getExtras().getSerializable("operator object");
        //Toast.makeText(this, mOPeratorObj.getmText() + "" + mOPeratorObj.getmPosition() + "", Toast.LENGTH_SHORT).show();
    }

    private void setTvTitle() {
        tvTopic.setText(mOPeratorObj.getmText());
    }

    private void createIntNums() {
        numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
        numbers.add(6);
        numbers.add(7);
        numbers.add(8);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mOPeratorObj.getmPosition() == 4) {
            disposable.dispose();
        }
        if (mOPeratorObj.getmPosition() == 5) {
            compositeDisposable.clear();
        }
    }
}
