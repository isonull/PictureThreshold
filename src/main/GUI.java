package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame {
	private File targetDir = new File("/Users/zijunyan/Desktop/PictureThreadhold/output");
	private String imageType = "png";

	private JPanel controlPanel;
	private JPanel displayPanel;

	private JFileChooser chooser;
	private JTextField enterMinDiff;
	private JTextField enterDiv;
	private JTextArea outputTextArea;
	private DefaultListModel<File> filepathListModel;
	private JList<File> filepathList;
	private JScrollPane filepathScrollPane;
	private JButton runButton;
	private JButton chooseButton;
	private JButton unchooseButton;

	public GUI() {
		setLayout(new BorderLayout());
		add(createDisplayPanel(), BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.SOUTH);
		setSize(500, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JFileChooser ch = new JFileChooser();
		ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ch.setDialogTitle("select target dir.");
		ch.setAcceptAllFileFilterUsed(false);
		int returnVal = ch.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			outputTextArea.append("Using this dir for output: " + ch.getSelectedFile().getPath() + "\n");
			targetDir = ch.getSelectedFile();
		} else {

		}
	}

	private JPanel createControlPanel() {
		controlPanel = new JPanel(new GridLayout(1, 0));

		chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("jpg and png only", "png", "jpg"));

		chooseButton = new JButton("choose");
		chooseButton.addActionListener(e -> {
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				outputTextArea.append("opened file: " + chooser.getSelectedFile().getPath() + "\n");
				filepathListModel.addElement(chooser.getSelectedFile());
			}
		});

		unchooseButton = new JButton("unchoose");
		unchooseButton.addActionListener(e -> {
			filepathListModel.remove(filepathList.getSelectedIndex());
		});

		runButton = new JButton("run");
		runButton.addActionListener(e -> {
			File tempImageFile;
			String targetPath;
			for (int i = 0; i < filepathListModel.size(); ++i) {
				tempImageFile = filepathListModel.getElementAt(i);
				targetPath = targetDir.getPath() + "/" + removeExtension(tempImageFile.getName()) + "." + imageType;
				outputTextArea.append("processing: " + tempImageFile.getName() + "\n");
				try {
					ImageIO.write(
							Thresholder.imageRGBtoBinary((Thresholder.readImage(tempImageFile)),
									Integer.parseInt(enterMinDiff.getText()), Integer.parseInt(enterDiv.getText())),
							imageType, new File(targetPath));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			outputTextArea.append("Done\n");
		});

		enterDiv = new JTextField();
		enterMinDiff = new JTextField();

		enterDiv.setText("16");
		enterMinDiff.setText("64");

		controlPanel.add(chooseButton);
		controlPanel.add(unchooseButton);
		controlPanel.add(runButton);
		controlPanel.add(enterDiv);
		controlPanel.add(enterMinDiff);

		return controlPanel;
	}

	private JPanel createDisplayPanel() {
		displayPanel = new JPanel(new BorderLayout());

		filepathListModel = new DefaultListModel<File>();
		filepathList = new JList<>(filepathListModel);
		filepathList.add(new ScrollPane());
		filepathScrollPane = new JScrollPane(filepathList);
		filepathScrollPane.setVerticalScrollBar(new JScrollBar());
		filepathScrollPane.setHorizontalScrollBar(new JScrollBar());
		outputTextArea = new JTextArea("Select a dir for output image file\n");
		outputTextArea.setEditable(false);

		displayPanel.add(filepathScrollPane, BorderLayout.WEST);
		displayPanel.add(outputTextArea, BorderLayout.CENTER);

		return displayPanel;
	}

	private String removeExtension(String fileName) {
		int dotIndex = fileName.indexOf('.');
		if (dotIndex < 0) {
			return fileName;
		} else {
			return fileName.substring(0, dotIndex);
		}
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
	}
}
