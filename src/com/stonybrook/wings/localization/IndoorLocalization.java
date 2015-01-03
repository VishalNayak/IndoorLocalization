package com.stonybrook.wings.localization;

import java.awt.EventQueue;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class IndoorLocalization {

	private JFrame frame = new JFrame();
	private float fraction = (float) 0.2;
	private String summaryFile = null;
	private BufferedWriter summary = null;
	private int ITERATIONS = 50;
	private String inputFileName = null;
	private JLabel lblNewLabel = null;
	private JTextPane txtPercentage = null;
	private JTextPane txtInputArff = null;
	private Label lblHidden = null;
	private JButton btnBrowse = null;
	private JLabel lblIterations = null;
	private JTextPane txtIterations = null;
	private JButton btnProcess = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IndoorLocalization window = new IndoorLocalization();
					// fraction = 0.3;
					// summaryFile = "resources/results/" + (int) (fraction *
					// 100)
					// + ".txt";
					// ITERATIONS = 50;
					// window.processFile("resources/input.arff");
					window.frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Error Occured! Verify input and retry....");
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public IndoorLocalization() {
		initialize();
	}

	public double findMean(ArrayList<Double> anArray) {
		ArrayList<Double> myArray = anArray;
		double arraySum = 0;
		double arrayAverage = 0;
		for (int x = 0; x < myArray.size(); x++)
			arraySum += myArray.get(x);
		arrayAverage = arraySum / myArray.size();
		return arrayAverage;
	}

	public double findMedian(ArrayList<Double> anArray) {
		ArrayList<Double> myArray = anArray;
		Collections.sort(myArray);
		int arrayLength = 0;
		double arrayMedian = 0;
		int currentIndex = 0;
		arrayLength = myArray.size();
		if (arrayLength % 2 != 0) {
			currentIndex = ((arrayLength / 2) + 1);
			arrayMedian = myArray.get(currentIndex - 1);
		} else {
			int indexOne = (arrayLength / 2);
			int indexTwo = arrayLength / 2 + 1;
			double arraysSum = myArray.get(indexOne - 1)
					+ myArray.get(indexTwo - 1);
			arrayMedian = arraysSum / 2;
		}
		return arrayMedian;
	}

	public ArrayList<Integer> findGreatestIndex(ArrayList<Integer> anArray) {
		ArrayList<Integer> myArray = anArray;
		ArrayList<Integer> numOfModes = new ArrayList<Integer>();
		Collections.sort(myArray);
		Collections.reverse(myArray);
		for (int x = 0; x < myArray.size() - 1; x++)
			if (myArray.get(x) == myArray.get(0))
				numOfModes.add(myArray.get(x));
		return numOfModes;
	}

	public int occuranceCount(ArrayList<Double> anArray, double check) {
		ArrayList<Double> originalArray = anArray;
		int occuranceCount = 0;
		for (int y = 0; y < originalArray.size(); y++)
			if (originalArray.get(y) == check)
				occuranceCount++;
		return occuranceCount;
	}

	public double findStandardDeviation(List<Double> numbers) {
		int size;
		double total = 0.0;
		size = numbers.size();
		for (int i = 0; i < size; i++)
			total += numbers.get(i);
		double mean = total / size;
		List<Double> deviations = new ArrayList<Double>();
		for (int i = 0; i < size; i++) {
			double cur = numbers.get(i);
			deviations.add(cur - mean);
		}

		List<Double> squaredDeviations = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			double cur = deviations.get(i);
			squaredDeviations.add(Math.pow(cur, 2));
		}

		double sum = 0.0;

		for (int i = 0; i < size; i++) {
			sum += squaredDeviations.get(i);
		}

		return Math.sqrt(sum / size);
	}

	private void test(String fileName) throws Exception {
		ArrayList<Double> precisions = new ArrayList<Double>();
		precisions.add(0.7872832369942196);
		precisions.add(0.8240396530359355);
		precisions.add(0.8167664670658683);
		precisions.add(0.8133802816901409);
		precisions.add(0.812200956937799);
		System.out.println("Precisions: " + precisions.toString());
		System.out.println("Median: " + findMedian(precisions));
		System.out.println("Mean: " + findMean(precisions));
		System.out.println("StandardDeviation: "
				+ findStandardDeviation(precisions));
	}

	private void processFile(String fileName) throws Exception {
		summary = new BufferedWriter(new FileWriter(summaryFile));
		summary.write("Summary file with " + txtPercentage.getText()
				+ "% train data and " + txtIterations.getText()
				+ " iterations.");
		summary.close();
		DataSource source = new DataSource(fileName);
		Instances randomTrainInstances = source.getDataSet();
		Instances randomTestInstances = source.getDataSet();
		randomTrainInstances.removeAll(randomTrainInstances);

		ArrayList<Double> precisions = new ArrayList<Double>();
		for (int j = 1; j <= ITERATIONS; j++) {
			summary = new BufferedWriter(new FileWriter(summaryFile, true));
			summary.write("\n~~~~~~~~~~~~~~~~~~~~~~~ Iteration: " + j
					+ " ~~~~~~~~~~~~~~~~~~~~~~~");
			lblHidden.setText("Processing iteration: " + j);
			Random rand = new Random();
			summary.write("\nTotal Instances: " + randomTestInstances.size());
			int numOfElements = (int) (fraction * randomTestInstances.size());
			for (int i = 0; i < numOfElements; i++) {
				int index = rand.nextInt(randomTestInstances.size());
				randomTrainInstances.add(randomTestInstances.get(index));
				randomTestInstances.remove(index);
			}
			summary.write("\nTrain Instances: " + randomTrainInstances.size());
			summary.write("\nTest Instances: " + randomTestInstances.size());
			randomTrainInstances.setClassIndex(randomTrainInstances
					.numAttributes() - 1);
			randomTestInstances.setClassIndex(randomTestInstances
					.numAttributes() - 1);
			NaiveBayes nb = new NaiveBayes();
			nb.buildClassifier(randomTrainInstances);
			String modelFolderName = "resources/results/"
					+ txtPercentage.getText();
			File mw = new File(modelFolderName);
			mw.mkdirs();
			String modelFileName = modelFolderName + "/" + j + ".model";
			BufferedWriter modelWriter = new BufferedWriter(new FileWriter(
					modelFileName));
			modelWriter.close();
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(modelFileName));
			oos.writeObject(nb);
			oos.flush();
			oos.close();
			Evaluation eval = new Evaluation(randomTrainInstances);
			// eval.crossValidateModel(nb, randomTrainInstances, 10, new
			// Random(1));
			eval.evaluateModel(nb, randomTestInstances);
			precisions.add(eval.precision(1));
			summary.write(eval.toSummaryString("\nClassification Summary:",
					true)
					+ "\nfMeasure:"
					+ eval.fMeasure(1)
					+ " precision:"
					+ eval.precision(1)
					+ " recall:"
					+ eval.recall(1)
					+ "\n"
					+ eval.toMatrixString());
			randomTestInstances.addAll(randomTrainInstances);
			randomTrainInstances.removeAll(randomTrainInstances);
			summary.close();
		}
		Collections.sort(precisions);
		summary = new BufferedWriter(new FileWriter(summaryFile, true));
		summary.write("Precisions:\n" + precisions.toString());
		summary.write("\nMean:" + findMean(precisions));
		summary.write("\nStandardDeviation:"
				+ findStandardDeviation(precisions));
		summary.write("\nMedian:" + findMedian(precisions));
		summary.close();
		System.out.println("Completed processing with " + fraction * 100 + "%");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame.setTitle("Indoor Localization");
		frame.setBounds(100, 100, 623, 390);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		lblNewLabel = new JLabel("Percentage:");
		lblNewLabel.setBounds(34, 101, 145, 54);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

		txtPercentage = new JTextPane();
		txtPercentage.setBounds(194, 101, 400, 54);
		txtPercentage.setText("Portion of input to be considered for training");

		txtInputArff = new JTextPane();
		txtInputArff.setBounds(193, 31, 401, 54);
		txtInputArff.setText("Browse for input 'arff' file");

		lblHidden = new Label("");
		lblHidden.setBounds(194, 256, 396, 51);
		frame.getContentPane().add(lblHidden);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(34, 31, 145, 54);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int rVal = jfc.showOpenDialog(frame);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					try {
						inputFileName = jfc.getSelectedFile().getPath();
						txtInputArff.setText(inputFileName);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"Error Occured! Verify input and retry....");
						ex.printStackTrace();
					}
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {

				}
			}
		});

		lblIterations = new JLabel("Iterations:");
		lblIterations.setBounds(34, 171, 145, 54);
		lblIterations.setHorizontalAlignment(SwingConstants.CENTER);

		txtIterations = new JTextPane();
		txtIterations.setBounds(194, 171, 400, 54);
		txtIterations.setText("Number of times precision has to be calculated");
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(lblIterations);
		frame.getContentPane().add(txtIterations);
		frame.getContentPane().add(lblNewLabel);
		frame.getContentPane().add(txtPercentage);
		frame.getContentPane().add(btnBrowse);
		frame.getContentPane().add(txtInputArff);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(null,
						"Verify the input.\nInput File: " + inputFileName
								+ "\nPercentage: " + txtPercentage.getText()
								+ "\nIterations: " + txtIterations.getText()
								+ "\nIs the input correct?", "Confirmation",
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					lblHidden.setText("Processing Data. Please wait..");
					try {
						fraction = Float.valueOf(txtPercentage.getText()) / 100;
						summaryFile = "resources/results/"
								+ txtPercentage.getText() + ".txt";
						System.out.println("fraction: " + fraction);
						System.out.println("summaryFile: " + summaryFile);
						ITERATIONS = Integer.valueOf(txtIterations.getText());
						System.out.println("iterations: " + ITERATIONS);
						processFile(inputFileName);
						lblHidden.setText("Processing Complete!");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,
								"Error Occured! Verify input and retry....");
						e1.printStackTrace();
					}
				}
			}
		});
		btnProcess.setBounds(30, 253, 145, 54);
		frame.getContentPane().add(btnProcess);
	}
}
