package org.bsu.gpx.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.bsu.gpx.calculator.CalculationParams;
import org.bsu.gpx.calculator.GPXCalculator;
import org.bsu.gpx.exception.GPXException;

public class GPXFrame extends JFrame {

    private static final String VERSION = "0.3";

    private static final String GPX_TRACK = "GPX Трек";
    private static final String GPX_BASE = "GPX Поправки";
    private static final String GPX_CALCULATE = "Рассчитать";
    private static final String OPEN_FILE = "Открыть файл";
    private static final String FILE_NOT_CHOOSEN = "Файл не выбран";
    private static final String CONVERSION_COMPLETE = "Преобразование завершено";
    private static final String UNEXPECTED_ERROR = "Непредвиденная ошибка";

    private final GPXCalculator calculator = new GPXCalculator();

    private File fileBase = null;
    private File fileTrack = null;
    private File fileDefault = null;

    private final JLabel labelBase = new JLabel(FILE_NOT_CHOOSEN);
    private final JLabel labelTrack = new JLabel(FILE_NOT_CHOOSEN);
    private final JLabel labelCalculate = new JLabel();

    private final JButton buttonBase = new JButton(GPX_BASE);
    private final JButton buttonTrack = new JButton(GPX_TRACK);
    private final JButton buttonCalculate = new JButton(GPX_CALCULATE);

    /**
     * 
     */
    private static final long serialVersionUID = 8178964946814014162L;

    public GPXFrame() {
        super("GPX converter V" + VERSION);
        this.setResizable(false);
        this.setBounds(100, 100, 600, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setVisible(true);
        this.initFields();
    }

    private void initFields() {
        Container contentPane = this.getContentPane();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);

        initTrackFields(contentPane);
        initCalculateFields(contentPane);
    }

    private void initCalculateFields(Container contentPane) {
        contentPane.add(buttonCalculate);
        buttonCalculate.setBounds(10, 70, 120, 20);
        buttonCalculate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }

        });
        contentPane.add(labelCalculate);
        labelCalculate.setBounds(140, 70, 400, 20);
    }

    private void initTrackFields(Container contentPane) {
        contentPane.add(buttonBase);
        buttonBase.setBounds(10, 10, 120, 20);
        buttonBase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fileBase = openFile();
                if (fileBase != null) {
                    labelBase.setText(fileBase.getAbsolutePath());
                }
            }

        });
        contentPane.add(buttonTrack);
        buttonTrack.setBounds(10, 40, 120, 20);
        buttonTrack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fileTrack = openFile();
                if (fileTrack != null) {
                    labelTrack.setText(fileTrack.getAbsolutePath());
                }
            }

        });
        contentPane.add(labelBase);
        labelBase.setBounds(140, 10, 400, 20);
        contentPane.add(labelTrack);
        labelTrack.setBounds(140, 40, 400, 20);
    }

    private void calculate() {
        labelCalculate.setText("");
        JFileChooser fileopen = createFileChooser();
        int result = fileopen.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = selectFileFromChooser(fileopen);
                calculator.calculate(fileBase, fileTrack, selectedFile, new CalculationParams());
                labelCalculate.setText(CONVERSION_COMPLETE);
            } catch (GPXException e) {
                labelCalculate.setText(e.getErrorCode().getLabel());
            } catch (Exception e) {
                e.printStackTrace();
                labelCalculate.setText(UNEXPECTED_ERROR);
            }
        }
    }

    private File openFile() {
        labelCalculate.setText("");
        JFileChooser fileopen = createFileChooser();
        int result = fileopen.showDialog(this, OPEN_FILE);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = selectFileFromChooser(fileopen);
            return selectedFile;
        }
        return null;
    }

    private File selectFileFromChooser(JFileChooser fileopen) {
        File selectedFile = fileopen.getSelectedFile();
        fileDefault = selectedFile.getParentFile();
        return selectedFile;
    }

    private JFileChooser createFileChooser() {
        JFileChooser fileopen = new JFileChooser();
        if (fileDefault != null) {
            fileopen.setCurrentDirectory(fileDefault);
        }
        return fileopen;
    }
}
