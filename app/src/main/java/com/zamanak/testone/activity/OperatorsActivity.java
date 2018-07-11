package com.zamanak.testone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.zamanak.testone.R;
import com.zamanak.testone.adapter.OperatorAdapter;
import com.zamanak.testone.interfaces.OperatorInterface;
import com.zamanak.testone.objects.OperatorsObj;
import com.zamanak.testone.presenter.OperatorsPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class OperatorsActivity extends AppCompatActivity implements OperatorInterface, OperatorAdapter.OnItemClickListener {


    private static final String TAG = "operators";
    private Context mContext;
    private OperatorsPresenter presenter;

    @BindView(R.id.rv_operators)
    RecyclerView rvOperators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);

        mContext = this;

        ButterKnife.bind(this);

        presenter = new OperatorsPresenter(this);
        presenter.prepareOperatorList();
        initOperatorRecylerView();


        // Let’s consider the example below. Here an Observable is created using fromArray() operator which emits the numbers from 1 to 20.
        //Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        /*Observable.fromArray(numbers).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: "+"All numbers emitted!");
            }
        });*/


        /**
         * Instead of writing the array of numbers manually, you can do the same using range(1, 20) operator as below.
         */
        Observable.range(1, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: " + "All numbers emitted");
            }
        });

        /**
         * chaining of operators : Let’s take same example of emitting numbers from 1 to 20. But in this case we want to filter out the even numbers along with we want to append a string at the end of each number.
         */
        Observable.range(1, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 2 == 0;
            }
        }).map(new Function<Integer, String>() {
            // first param Integer is the input parameter of apply() and the second param String is the output of the apply()
            @Override
            public String apply(Integer integer) throws Exception {
                return integer + "is even number";
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: " + d);
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
                Log.d(TAG, "onComplete: " + "All even numbers emitted");
            }
        });
    }

    private void initOperatorRecylerView() {
        rvOperators.setHasFixedSize(true);
        rvOperators.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // if you want add some item decoration plz add here
    }

    @Override
    public void getOperatorsList(ArrayList<OperatorsObj> operatorList) {
        rvOperators.setAdapter(new OperatorAdapter(operatorList, this));
    }

    @Override
    public void onClick(OperatorsObj obj) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("operator object", obj);
        startActivity(new Intent(this, DetailOperatorActivity.class).putExtras(bundle));
    }
}
