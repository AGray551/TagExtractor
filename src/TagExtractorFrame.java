import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class TagExtractorFrame extends JFrame {

    JPanel frame;
    JPanel topPanel;
    JTextField fileName;
    JScrollPane tagDisplay;


    JPanel botPanel;
    JButton scanFile;
    JButton tagFile;
    JButton save;
    JButton quit;

    public TagExtractorFrame() {
        setLayout(new BorderLayout());
        frame = new JPanel();

        createTopPanel();
        frame.add(topPanel);

        createBotPanel();
        frame.add(botPanel);

        add(frame);
        setTitle("Tag Extractor");
        setLocationRelativeTo(null);
        setLocation(500, 200);
        setSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createTopPanel() {
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        fileName = new JTextField("File Name: ");
        fileName.setBorder(null);
        fileName.setEditable(false);
        topPanel.add(fileName);

        tagDisplay = new JScrollPane();
        tagDisplay.setPreferredSize(new Dimension(600, 400));
        topPanel.add(tagDisplay);

        add(topPanel, BorderLayout.NORTH);
    }

    private Set<String> loadStopWords() {
        Set<String> stopWords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File("English Stop Words.txt"))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Stop words file not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return stopWords;
    }

    private void createBotPanel() {
        botPanel = new JPanel();

        scanFile = new JButton("Insert Text File");
        scanFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Map<String, Integer> wordFreq = new TreeMap<>();
                Set<String> stopWords = loadStopWords();
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    fileName.setText("File Name: " + file.getName());
                    try (Scanner scanner = new Scanner(file)) {
                        while (scanner.hasNext()) {
                            String word = scanner.next().replaceAll("[^a-zA-Z]", "").toLowerCase();
                            if (!stopWords.contains(word)) {
                                int count = wordFreq.getOrDefault(word, 0);
                                wordFreq.put(word, count + 1);
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(TagExtractorFrame.this, "File not found: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                StringBuilder tagOutput = new StringBuilder();
                tagOutput.append("Word Frequency (sorted alphabetically):\n");
                for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
                    tagOutput.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
                }
                JTextArea textArea = new JTextArea();
                textArea.setText(tagOutput.toString());
                tagDisplay.setViewportView(textArea);
            }
        });


        botPanel.add(scanFile);

        tagFile = new JButton("Insert Tag File");
        tagFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("English Stop Words.txt"));
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (Scanner scanner = new Scanner(file)) {
                        Set<String> stopWords = new HashSet<>();
                        while (scanner.hasNextLine()) {
                            stopWords.add(scanner.nextLine().toLowerCase());
                        }
                        System.out.println("Stop Words:");
                        for (String word : stopWords) {
                            System.out.println(word);
                        }
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(TagExtractorFrame.this, "Stop words file not found: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        botPanel.add(tagFile);

        save = new JButton("Save Tag Output File");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (PrintWriter writer = new PrintWriter(file)) {
                        JTextArea textArea = (JTextArea) tagDisplay.getViewport().getView();
                        String tagOutput = textArea.getText();
                        writer.print(tagOutput);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(TagExtractorFrame.this, "Error saving file: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        botPanel.add(save);

        quit = new JButton("Quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        botPanel.add(quit);
        add(botPanel, BorderLayout.SOUTH);
    }
}
