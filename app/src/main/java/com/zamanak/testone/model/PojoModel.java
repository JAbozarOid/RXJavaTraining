package com.zamanak.testone.model;

import android.annotation.SuppressLint;
import android.util.Log;
import com.zamanak.testone.objects.OperatorsObj;
import com.zamanak.testone.presenter.OperatorsPresenter;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PojoModel {

    private final String TAG = "Operators";

    private OperatorsPresenter mOperatorPresenter;
    private CompositeDisposable compositeDisposable;
    private Observable<OperatorsObj> operatorsObjObservable;
    private Observer<ArrayList<OperatorsObj>> operatorsObjObserver;


    // default constructor
    public PojoModel() {
    }

    public PojoModel(OperatorsPresenter mOperatorPresenter) {
        this.mOperatorPresenter = mOperatorPresenter;
        this.compositeDisposable = new CompositeDisposable();
        //this.operatorsObjObservable = getOperatorObservable(setOperatorDataList());
        //this.operatorsObjObserver = getOperatorObserver();
    }

    @SuppressLint("CheckResult")
    public void setRXOperatorList() {
        operatorsObjObservable
                .just(setOperatorDataList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<OperatorsObj>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<OperatorsObj> list) {
                        mOperatorPresenter.getOperatorList(list);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public ArrayList<OperatorsObj> setOperatorDataList() {
        ArrayList<OperatorsObj> operatorsList = new ArrayList<OperatorsObj>();
        operatorsList.add(new OperatorsObj(1,"Just"));
        operatorsList.add(new OperatorsObj(2,"From"));
        operatorsList.add(new OperatorsObj(3,"Range"));
        operatorsList.add(new OperatorsObj(4,"Buffer"));
        operatorsList.add(new OperatorsObj(5,"Debounce"));
        operatorsList.add(new OperatorsObj(6,"Filter"));
        operatorsList.add(new OperatorsObj(7,"Repeat"));
        operatorsList.add(new OperatorsObj(8,"Skip"));
        operatorsList.add(new OperatorsObj(9,"Take"));
        operatorsList.add(new OperatorsObj(10,"TakeLast"));
        operatorsList.add(new OperatorsObj(11,"Distinct"));
        operatorsList.add(new OperatorsObj(12,"Count"));
        operatorsList.add(new OperatorsObj(13,"Reduce"));
        operatorsList.add(new OperatorsObj(14,"Max"));
        operatorsList.add(new OperatorsObj(15,"Min"));
        operatorsList.add(new OperatorsObj(16,"Sum"));
        operatorsList.add(new OperatorsObj(17,"Average"));
        operatorsList.add(new OperatorsObj(18,"Concat"));
        operatorsList.add(new OperatorsObj(19,"Merge"));
        operatorsList.add(new OperatorsObj(20,"Map"));
        operatorsList.add(new OperatorsObj(21,"FlatMap"));
        operatorsList.add(new OperatorsObj(22,"ConcatMap"));
        operatorsList.add(new OperatorsObj(23,"SwitchMap"));

        return operatorsList;
    }

    private Observable<OperatorsObj> getOperatorObservable(final ArrayList<OperatorsObj> operatorsList) {
        return Observable.create(new ObservableOnSubscribe<OperatorsObj>() {
            @Override
            public void subscribe(ObservableEmitter<OperatorsObj> emitter) throws Exception {
                for (OperatorsObj operators : operatorsList) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(operators);
                    }
                }
                if (emitter.isDisposed()) {
                    emitter.onComplete();
                }

            }
        });
    }

    private Observer<OperatorsObj> getOperatorObserver() {
        return new Observer<OperatorsObj>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: " + d);
            }

            @Override
            public void onNext(OperatorsObj operatorsObj) {
                Log.d(TAG, "onNext: " + operatorsObj.getmText());

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: " + "all operators emitted");
            }
        };
    }




}
