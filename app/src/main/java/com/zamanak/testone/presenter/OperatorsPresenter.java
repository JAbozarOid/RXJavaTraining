package com.zamanak.testone.presenter;

import com.zamanak.testone.interfaces.OperatorInterface;
import com.zamanak.testone.model.PojoModel;
import com.zamanak.testone.objects.OperatorsObj;

import java.util.ArrayList;

public class OperatorsPresenter {

    private PojoModel pojoModel;
    private OperatorInterface mOperatorInterface;

    public OperatorsPresenter(OperatorInterface operatorInterface) {
        this.mOperatorInterface = operatorInterface;
        this.pojoModel = new PojoModel(this);
    }

    public void prepareOperatorList() {
        pojoModel.setRXOperatorList();
    }

    public void getOperatorList(ArrayList<OperatorsObj> operatorsObjs) {
        this.mOperatorInterface.getOperatorsList(operatorsObjs);
    }
}
