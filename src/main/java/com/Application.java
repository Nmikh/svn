package com;

import com.models.MatrixModel;
import com.services.SymptomsService;
import libsvm.svm_model;

import java.io.IOException;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        SymptomsService symptomsService = new SymptomsService();
        svm_model svmModel = symptomsService.svmTrain();
        MatrixModel matrixModel = symptomsService.evaluateAllInstances("test.txt", svmModel);
        System.out.println(matrixModel);
    }
}
