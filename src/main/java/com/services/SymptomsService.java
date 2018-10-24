package com.services;

import com.DAO.SimptomsDAO;
import com.models.DataObject;
import com.models.MatrixModel;
import libsvm.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public class SymptomsService {
    SimptomsDAO simptomsDAO;

    public SymptomsService() throws SQLException, IOException, ClassNotFoundException {
        this.simptomsDAO = new SimptomsDAO();
    }

    public svm_model svmTrain() throws SQLException {
        System.out.println("Start learning");
        ArrayList<DataObject> dataset = (ArrayList) simptomsDAO.findAllObjects();
        System.out.println("dataset Size " + dataset.size());
        int recordSize = dataset.size();

        double nodeValues[][] = new double[recordSize][]; //jagged array used to store values
        int nodeIndexes[][] = new int[recordSize][];//jagged array used to store node indexes
        double nodeClassLabels[] = new double[recordSize];//store class lavels

        //Now store data values
        for (int i = 0; i < dataset.size(); i++) {
            int dataClass;
            if (dataset.get(i).getCategory() == 2)
                dataClass = 1;
            else
                dataClass = -1;
            nodeClassLabels[i] = dataClass;

            LinkedList<Integer> listIndx = new LinkedList<Integer>();
            LinkedList<Double> listVal = new LinkedList<Double>();

            listIndx.add(1);
            listVal.add((double) dataset.get(i).getClumpThickness());

            listIndx.add(2);
            listVal.add((double) dataset.get(i).getUniformityOfCellSize());

            listIndx.add(3);
            listVal.add((double) dataset.get(i).getUniformityOfCellShape());

            listIndx.add(4);
            listVal.add((double) dataset.get(i).getMarginalAdhesion());

            listIndx.add(5);
            listVal.add((double) dataset.get(i).getSingleEpithelialCellSize());

            listIndx.add(6);
            listVal.add((double) dataset.get(i).getBareNuclei());

            listIndx.add(7);
            listVal.add((double) dataset.get(i).getBlandChromatin());

            listIndx.add(8);
            listVal.add((double) dataset.get(i).getNormalNucleoli());

            listIndx.add(9);
            listVal.add((double) dataset.get(i).getMitoses());

            if (listVal.size() > 0) {
                nodeValues[i] = new double[listVal.size()];
                nodeIndexes[i] = new int[listIndx.size()];
            }
            for (int m = 0; m < listVal.size(); m++) {
                nodeIndexes[i][m] = listIndx.get(m);
                nodeValues[i][m] = listVal.get(m);
            }

        }

        svm_problem prob = new svm_problem();
        int dataCount = recordSize;
        prob.y = new double[dataCount];
        prob.l = dataCount;
        prob.x = new svm_node[dataCount][];

        for (int i = 0; i < dataCount; i++) {
            prob.y[i] = nodeClassLabels[i];
            double[] values = nodeValues[i];
            int[] indexes = nodeIndexes[i];
            prob.x[i] = new svm_node[values.length];
            for (int j = 0; j < values.length; j++) {
                svm_node node = new svm_node();
                node.index = indexes[j];
                node.value = values[j];
                prob.x[i][j] = node;
            }
        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 1;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 20000;
        param.eps = 0.001;

        svm_model model = svm.svm_train(prob, param);

        return model;
    }

    //write code to test all instances from given file
    public MatrixModel evaluateAllInstances(String fileName, svm_model model) throws IOException {
        //read data from file
        //read the data from file and put it in the train module

        System.out.println("Start testing");
        FileReader input = new FileReader(fileName);
        BufferedReader bufRead = new BufferedReader(input);
        String line = null;

        MatrixModel matrixModel = new MatrixModel(0, 0, 0, 0, 0);

        ArrayList<DataObject> dataSet = new ArrayList<DataObject>();

        while ((line = bufRead.readLine()) != null) {
            String[] array = line.split(",");
            DataObject o = new DataObject(
                    Integer.parseInt(array[2]),
                    Integer.parseInt(array[3]),
                    Integer.parseInt(array[4]),
                    Integer.parseInt(array[5]),
                    Integer.parseInt(array[6]),
                    Integer.parseInt(array[7]),
                    Integer.parseInt(array[8]),
                    Integer.parseInt(array[9]),
                    Integer.parseInt(array[10]),
                    Integer.parseInt(array[1])
            );
            if (o.getCategory() == 2)
                o.setCategory(1);
            else
                o.setCategory(-1);
            dataSet.add(o);
        }

        int recordSize = dataSet.size();
        System.out.println("DataSet Size: " + dataSet.size());

        double nodeValues[][] = new double[recordSize][]; //jagged array used to store values
        int nodeIndexes[][] = new int[recordSize][];//jagged array used to store node indexes

        for (int i = 0; i < dataSet.size(); i++) {

            LinkedList<Integer> listIndx = new LinkedList<Integer>();
            LinkedList<Double> listVal = new LinkedList<Double>();

            listIndx.add(1);
            listVal.add((double) dataSet.get(i).getClumpThickness());

            listIndx.add(2);
            listVal.add((double) dataSet.get(i).getUniformityOfCellSize());

            listIndx.add(3);
            listVal.add((double) dataSet.get(i).getUniformityOfCellShape());

            listIndx.add(4);
            listVal.add((double) dataSet.get(i).getMarginalAdhesion());

            listIndx.add(5);
            listVal.add((double) dataSet.get(i).getSingleEpithelialCellSize());

            listIndx.add(6);
            listVal.add((double) dataSet.get(i).getBareNuclei());

            listIndx.add(7);
            listVal.add((double) dataSet.get(i).getBlandChromatin());

            listIndx.add(8);
            listVal.add((double) dataSet.get(i).getNormalNucleoli());

            listIndx.add(9);
            listVal.add((double) dataSet.get(i).getMitoses());

            if (listVal.size() > 0) {
                nodeValues[i] = new double[listVal.size()];
                nodeIndexes[i] = new int[listIndx.size()];
            }
            for (int m = 0; m < listVal.size(); m++) {
                nodeIndexes[i][m] = listIndx.get(m);
                nodeValues[i][m] = listVal.get(m);
            }
        }

        int positive = 0;
        int negative = 0;
        for (int i = 0; i < recordSize; i++) {
            int tmpIndexes[] = nodeIndexes[i];
            double tmpValues[] = nodeValues[i];
            double v = evaluateSingleInstance(tmpIndexes, tmpValues, model);
            System.out.println(" Expected: " + (float) dataSet.get(i).getCategory());

            if ((float) dataSet.get(i).getCategory() == 1.0) {
                positive++;
                if (v == 1.0) {
                    matrixModel.setTruePositive(matrixModel.getTruePositive() + 1);
                } else {
                    matrixModel.setFalseNegative(matrixModel.getFalseNegative() + 1);
                }
            } else {
                negative++;
                if (v == -1.0) {
                    matrixModel.setTrueNegative(matrixModel.getTrueNegative() + 1);
                } else {
                    matrixModel.setFalsePositive(matrixModel.getFalsePositive() + 1);
                }
            }
        }

        matrixModel.setAccuracy(
                ((float) matrixModel.getTruePositive() + (float) matrixModel.getTrueNegative())
                        /
                        ((float) positive + (float) negative));
        return matrixModel;
    }

    //write the code to test single feature each time by using SVM
    public double evaluateSingleInstance(int[] indexes, double[] values, svm_model model) {
        svm_node[] nodes = new svm_node[values.length];
        for (int i = 0; i < values.length; i++) {
            svm_node node = new svm_node();
            node.index = indexes[i];
            node.value = values[i];
            nodes[i] = node;
        }

        int totalClasses = svm.svm_get_nr_class(model);
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(model, labels);

        double[] probEstimates = new double[totalClasses];
        double v = svm.svm_predict_probability(model, nodes, probEstimates);

        for (int i = 0; i < totalClasses; i++) {
            System.out.print("(" + labels[i] + ":" + probEstimates[i] + ")");
        }
        System.out.print(" Prediction: " + v);
        return v;
    }

}
