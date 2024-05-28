package demo;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.io.FileWriter;

public class MyGraph extends JFrame {

    public void randomWalkWithUserChoice() {
        if (graph.getNodeCount() == 0) {
            System.out.println("Graph is empty. Cannot perform random walk.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        try (FileWriter writer = new FileWriter("random_walk_choice_result.txt")) {

            System.out.println("Do you want to continue?(y or n)");
            String input = scanner.nextLine();
            while (input.equals("y")) {
                org.graphstream.graph.Node currentNode = graph.getNode(random.nextInt(graph.getNodeCount()));
                List<String> walkResults = new ArrayList<>();
                Set<String> visitedEdges = new HashSet<>();
                Set<String> visitedNodes = new HashSet<>();
                String lastVisitedNode = null;

                StringBuilder walkResult = new StringBuilder();

                while (true) {
                    walkResult.append(currentNode.getId()).append(" ");
                    walkResults.add(currentNode.getId());
                    visitedNodes.add(currentNode.getId());

                    org.graphstream.graph.Edge[] edges = currentNode.getLeavingEdgeSet().toArray(new org.graphstream.graph.Edge[0]);
                    if (edges.length == 0 || visitedNodes.size() == graph.getNodeCount()) {
                        break; // No outgoing edges or all nodes visited, end walk
                    }

                    // Randomly select an edge
                    org.graphstream.graph.Edge nextEdge = edges[random.nextInt(edges.length)];

                    // Output current random walk result
                    walkResult.append("[").append(nextEdge.getId()).append("] ");
                    walkResult.append(nextEdge.getTargetNode().getId()).append(" ");

                    // Check if the selected edge has been visited before
                    if (visitedEdges.contains(nextEdge.getId())) {
                        lastVisitedNode = nextEdge.getTargetNode().getId();
                        break;
                    }

                    visitedEdges.add(nextEdge.getId());
                    currentNode = nextEdge.getTargetNode();
                }

                // Add the last visited node to the results
                if (lastVisitedNode != null) {
                    walkResults.add(lastVisitedNode);
                }

                // Write the results to file
                writer.write(String.join(" ", walkResults));
                writer.write(System.lineSeparator()); // Add newline after each walk
                System.out.println("Do you want to continue?");
                input = scanner.nextLine();
            }
            System.out.println( " random walks have been saved to 'random_walk_choice_result.txt'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Perform random walks multiple times and save results to a file
    public void randomWalkMultipleTimes(int times) {
        if (graph.getNodeCount() == 0) {
            System.out.println("Graph is empty. Cannot perform random walk.");
            return;
        }
        Random random = new Random();

        try (FileWriter writer = new FileWriter("random_walk_result.txt")) {
            for (int i = 0; i < times; i++) {
                org.graphstream.graph.Node currentNode = graph.getNode(random.nextInt(graph.getNodeCount()));
                List<String> walkResults = new ArrayList<>();
                Set<String> visitedEdges = new HashSet<>();
                Set<String> visitedNodes = new HashSet<>();
                String lastVisitedNode = null;

                StringBuilder walkResult = new StringBuilder();

                while (true) {
                    walkResult.append(currentNode.getId()).append(" ");
                    walkResults.add(currentNode.getId());
                    visitedNodes.add(currentNode.getId());

                    org.graphstream.graph.Edge[] edges = currentNode.getLeavingEdgeSet().toArray(new org.graphstream.graph.Edge[0]);
                    if (edges.length == 0 || visitedNodes.size() == graph.getNodeCount()) {
                        break; // No outgoing edges or all nodes visited, end walk
                    }

                    // Randomly select an edge
                    org.graphstream.graph.Edge nextEdge = edges[random.nextInt(edges.length)];

                    // Output current random walk result
                    walkResult.append("[").append(nextEdge.getId()).append("] ");
                    walkResult.append(nextEdge.getTargetNode().getId()).append(" ");

                    // Check if the selected edge has been visited before
                    if (visitedEdges.contains(nextEdge.getId())) {
                        lastVisitedNode = nextEdge.getTargetNode().getId();
                        break;
                    }

                    visitedEdges.add(nextEdge.getId());
                    currentNode = nextEdge.getTargetNode();
                }

                // Add the last visited node to the results
                if (lastVisitedNode != null) {
                    walkResults.add(lastVisitedNode);
                }

                // Write the results to file
                writer.write(String.join(" ", walkResults));
                writer.write(System.lineSeparator()); // Add newline after each walk
            }

            System.out.println(times + " random walks have been saved to 'random_walk_result.txt'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Calculate shortest path between two words using Dijkstra's algorithm
    public void calculateShortestPath(String startWord, String endWord) {
        if (!graph.getNodeSet().contains(graph.getNode(startWord))) {
            System.out.println("No such word in the graph!");
            return;
        }

        // If endWord is empty, compute shortest paths to all other words
        if (endWord.isEmpty()) {
            for (org.graphstream.graph.Node node : graph) {
                if (!node.getId().equals(startWord)) {
                    calculateShortestPath(startWord, node.getId());
                }
            }
            return;
        }

        // Initialize distances to infinity and the start node to 0
        Map<String, Integer> distances = new HashMap<>();
        for (org.graphstream.graph.Node node : graph) {
            distances.put(node.getId(), Integer.MAX_VALUE);
        }
        distances.put(startWord, 0);

        // Priority queue to store nodes with their distances
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        pq.add(startWord);

        // Map to store parent nodes for each node in the shortest path
        Map<String, String> parentMap = new HashMap<>();

        // Dijkstra's algorithm
        while (!pq.isEmpty()) {
            String current = pq.poll();
            for (org.graphstream.graph.Edge edge : graph.getNode(current).getEachLeavingEdge()) {
                String next = edge.getTargetNode().getId();
                int weight = edge.getAttribute("ui.label");
                int newDistance = distances.get(current) + weight;
                if (newDistance < distances.get(next)) {
                    distances.put(next, newDistance);
                    pq.add(next);
                    parentMap.put(next, current);
                }
            }
        }

        // Reconstruct the shortest path
        List<String> shortestPath = new ArrayList<>();
        String current = endWord;
        while (current != null) {
            shortestPath.add(current);
            current = parentMap.get(current);
        }
        Collections.reverse(shortestPath);

        // Highlight the shortest path in the graph
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            String word1 = shortestPath.get(i);
            String word2 = shortestPath.get(i + 1);
            String edgeId = word1 + "->" + word2;
            org.graphstream.graph.Edge edge = graph.getEdge(edgeId);
            if (edge != null) {
                edge.setAttribute("ui.style", "fill-color: red;");
            }
        }

        // Display the shortest path and its length
        System.out.println("Shortest path from " + startWord + " to " + endWord + ":");
        System.out.println(String.join(" -> ", shortestPath));
        int pathLength = distances.get(endWord);
        System.out.println("Path length: " + pathLength);
    }


    private Graph graph;

    public MyGraph(String filePath) {
        super("Text to Graph");

        // Create graph object
        graph = new SingleGraph("Text Graph");

        try {
            readTextFile(filePath); // Read text data from the specified file path and generate the graph
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        // Set node style
        for (org.graphstream.graph.Node node : graph) {
            node.setAttribute("ui.label", node.getId()); // Set node display as node text information
            node.setAttribute("ui.style", "shape:circle;size:30px;fill-color:green; text-color:black; text-size:18px;"); // Set node style, increase font size
        }

        // Set edge style
        for (org.graphstream.graph.Edge edge : graph.getEachEdge()) {
            int weight = edge.getAttribute("ui.label"); // Get edge weight

            // Set edge weight font size and color
            edge.setAttribute("ui.style", "text-size:" + (18 + weight) + "px; text-color:black;");
        }

        // Create a Swing view panel
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        ViewPanel viewPanel = viewer.addDefaultView(false);
        viewPanel.setPreferredSize(new Dimension(800, 600));

        getContentPane().add(viewPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void readTextFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String content;
        String previousWord = null;
        Map<String, Integer> wordCounts = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder();

        while ((content = br.readLine()) != null) {
            stringBuilder.append(content);
        }

        br.close();
        content = stringBuilder.toString().toLowerCase().replaceAll("[^a-z ]", " ").trim();
        String[] words = content.split(" ");

        String[] filteredWords = Arrays.stream(words)
                .filter(word -> !word.isEmpty())
                .toArray(String[]::new);

        for (String word : filteredWords) {
            if (graph.getNode(word) == null) {
                graph.addNode(word);
            }
        }

        for (String word : words) {
            if (word.isEmpty()) continue;
            if (previousWord != null) {
                String edgeId = previousWord + "->" + word;
                if (graph.getEdge(edgeId) == null) {
                    graph.addEdge(edgeId, previousWord, word, true).setAttribute("ui.label", 1);
                } else {
                    int weight = Integer.parseInt(graph.getEdge(edgeId).getAttribute("ui.label").toString());
                    graph.getEdge(edgeId).setAttribute("ui.label", weight + 1);
                }
            }
            previousWord = word;
        }
    }

    public String queryBridgeWords(String word1, String word2) {
        if (graph.getNode(word1) == null || graph.getNode(word2) == null) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        List<String> bridgeWords = new ArrayList<>();
        for (org.graphstream.graph.Edge edge : graph.getNode(word1).getEdgeSet()) {
            String bridgeWord = edge.getOpposite(graph.getNode(word1)).getId();
            if (graph.getNode(bridgeWord).hasEdgeBetween(word2)) {
                bridgeWords.add(bridgeWord);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + "\"" + word1 + "\"" + " to " + "\"" + word2 + "\"";
        } else {
            return "The bridge words from " + "\"" + word1 + "\"" + " to " + "\"" + word2 + "\"" + " is: " + String.join(", ", bridgeWords) + ".";
        }
    }

    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().replaceAll("[^a-z ]", " ").trim().split("\\s+");
        if (words.length < 2) {
            return inputText; // Return input text directly if fewer than two words
        }

        StringBuilder newText = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");
            String word1 = words[i];
            String word2 = words[i + 1];

            if (graph.getNode(word1) == null || graph.getNode(word2) == null) {
                continue; // Skip if one of the words does not exist in the graph
            }

            List<String> bridgeWords = new ArrayList<>();
            for (org.graphstream.graph.Edge edge : graph.getNode(word1).getEdgeSet()) {
                String bridgeWord = edge.getOpposite(graph.getNode(word1)).getId();
                if (graph.getNode(bridgeWord).hasEdgeBetween(word2)) {
                    bridgeWords.add(bridgeWord);
                }
            }

            if (!bridgeWords.isEmpty()) {
                String bridgeWord = bridgeWords.get(rand.nextInt(bridgeWords.size()));
                newText.append(bridgeWord).append(" ");
            }
        }
        newText.append(words[words.length - 1]); // Add the last word

        return newText.toString();
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter file path: ");
        String filePath = "C:\\Users\\Lenovo\\Desktop\\ruanjiangongcheng.txt";

        MyGraph myGraph = new MyGraph(filePath);

        while (true) {
            System.out.println("Select operation: 1. Query bridge words 2. Generate text based on bridge words 3. Calculate shortest path 4. Random walk (10 times) 5. Random walk with user choice 6. Exit");
            String choice = scanner.nextLine().trim();

            if (choice.equals("6")) {
                break;
            }

            switch (choice) {
                case "1":
                    System.out.print("Enter the first word: ");
                    String word1 = scanner.nextLine().trim();
                    System.out.print("Enter the second word: ");
                    String word2 = scanner.nextLine().trim();
                    String result = myGraph.queryBridgeWords(word1, word2);
                    System.out.println(result);
                    break;
                case "2":
                    System.out.print("Enter a new line of text: ");
                    String inputText = scanner.nextLine().trim();
                    String newText = myGraph.generateNewText(inputText);
                    System.out.println("New text: " + newText);
                    break;
                case "3":
                    System.out.print("Enter the start word: ");
                    String startWord = scanner.nextLine().trim();
                    System.out.print("Enter the end word: ");
                    String endWord = scanner.nextLine().trim();
                    myGraph.calculateShortestPath(startWord, endWord);
                    break;
                case "4":
                    myGraph.randomWalkMultipleTimes(10); // Perform 10 random walks
                    break;
                case "5":
                    myGraph.randomWalkWithUserChoice();
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    break;
            }
        }

        scanner.close();
    }
}