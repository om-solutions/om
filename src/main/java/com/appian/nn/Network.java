package com.appian.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

public class Network {
	public Double min = Double.MAX_VALUE;
	public Double max = 0d;
	public DataSet trainingSet = new DataSet(5, 1);
	private static final int maxIterations = 1000;
	private static final double learningRate = 0.5;
	private static final double maxError = 0.00001;
	NeuralNetwork<BackPropagation> neuralNetwork;
	public NeuralNetwork<BackPropagation> getNeuralNetwork() {
		return neuralNetwork;
	}

	public void setNeuralNetwork(NeuralNetwork<BackPropagation> neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}

	public int slidingWindowSize;


	public Double normalizeValue(Double input) {
		return (input - min) / (max - min) * 0.8 + 0.1;
	}

	public double deNormalizeValue(double input) {
		return min + (input - 0.1) * (max - min) / 0.8;
	}
	public void clearTrainingSet()
	{
		trainingSet = new DataSet(slidingWindowSize, 1);
		this.neuralNetwork=createNewNeuralNetwork();
	}
	public Network(int slidingWindowSize){
		this.slidingWindowSize=slidingWindowSize;
		this.neuralNetwork=createNewNeuralNetwork();
	}
	public NeuralNetwork<BackPropagation> createNewNeuralNetwork() {
		
		NeuralNetwork<BackPropagation> neuralNetwork=new MultiLayerPerceptron(slidingWindowSize,2*slidingWindowSize+1, 1);
		SupervisedLearning learningRule = neuralNetwork.getLearningRule();
		learningRule.setMaxError(maxError);
		learningRule.setLearningRate(learningRate);
		learningRule.setMaxIterations(maxIterations);
		return neuralNetwork;
	}

	public ArrayList<Integer> trainNetworkFromData(ArrayList<Double> values) {

		ArrayList<Double> expectedValue;
		ArrayList<Double> trainingSetArray=new ArrayList<>();
		ArrayList<Integer> missingValues=new ArrayList<Integer>();
		for(int i=0;i<values.size()-1;i++)
		{
			trainingSetArray.add(this.normalizeValue(values.get(i)));
			if(trainingSetArray.size()==slidingWindowSize)
			{
				expectedValue=new ArrayList<>();
				expectedValue.add(this.normalizeValue(values.get(i+1)));
				if(expectedValue.contains(null))
				{
					missingValues.add(i+1);
				}
				else if(!trainingSetArray.contains(null))
				{
					this.trainingSet.addRow(new DataSetRow(trainingSetArray,expectedValue));
				}
				trainingSetArray.remove(0);
			}
		}
		this.neuralNetwork.learn(trainingSet);
		return missingValues;
	}

	public HashMap<Integer, Double> addMissingValues(ArrayList<Double> values,
			ArrayList<Integer> missingValues) {
		HashMap<Integer,Double> predictedValues=new HashMap<Integer,Double>();
		for(Integer missingIndex:missingValues)
		{
			double[] inputSet=new double[slidingWindowSize];
			if(missingIndex>slidingWindowSize)
			{
				boolean setContainsNull=false;
				for(int i=missingIndex-slidingWindowSize;i<missingIndex;i++)
				{
					if(values.get(i)==null)
					{
						if(predictedValues.containsKey(i))
							inputSet[i-missingIndex+slidingWindowSize]=normalizeValue(predictedValues.get(i));
						else
						{
							setContainsNull=true;
							break;
						}
					}
					else
						inputSet[i-missingIndex+slidingWindowSize]=normalizeValue(values.get(i));
				}
				if(setContainsNull==false)
				{
					this.neuralNetwork.setInput(inputSet);
					this.neuralNetwork.calculate();
					double[] networkOutput = neuralNetwork.getOutput();
					predictedValues.put(missingIndex, deNormalizeValue(networkOutput[0]));
					this.trainingSet.addRow(inputSet, networkOutput);
					this.neuralNetwork.learn(trainingSet);
				}
			}
		}
		return predictedValues;
	}
	
	public double nextVal(double[] previousValuesList)
	{
		this.getNeuralNetwork().setInput(previousValuesList);
		this.getNeuralNetwork().calculate();
		double nextVal=this.getNeuralNetwork().getOutput()[0];
		this.trainingSet.addRow(previousValuesList, new double[]{nextVal});
		this.getNeuralNetwork().learn(this.trainingSet);
		return deNormalizeValue(nextVal);
	}

}
