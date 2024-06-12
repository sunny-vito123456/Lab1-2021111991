package demo;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;




public class MyGraph extends JFrame {

  public void randomWalkWithUserChoice() {
    if (graph.getNodeCount() == 0) {
      System.out.println("Graph is empty. Cannot perform random walk.");
      return;
    }

    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    SecureRandom secureRandom = new SecureRandom();

    org.graphstream.graph.Node currentNode = graph.getNode(secureRandom.nextInt(graph.getNodeCount()));
    List<String> walkResults = new ArrayList<>();
    Set<String> visitedEdges = new HashSet<>();
    Set<String> visitedNodes = new HashSet<>();
    String lastVisitedNode = null;

    try {
      // Open FileWriter in append mode
      FileWriter writer = new FileWriter("random_walk.txt", StandardCharsets.UTF_8, true);

      while (true) {
        System.out.println("Current node is " + currentNode.getId());

        StringBuilder walkResult = new StringBuilder();
        walkResult.append(currentNode.getId()).append(" ");
        walkResults.add(currentNode.getId());
        visitedNodes.add(currentNode.getId());

        org.graphstream.graph.Edge[] edges = currentNode.getLeavingEdgeSet().toArray(new org.graphstream.graph.Edge[0]);
        if (edges.length == 0 || visitedNodes.size() == graph.getNodeCount()) {
          break; // No outgoing edges or all nodes visited, end walk
        }

        // Randomly select an edge
        org.graphstream.graph.Edge nextEdge = edges[secureRandom.nextInt(edges.length)];

        // Output current random walk result
        walkResult.append("[").append(nextEdge.getId()).append("] ");
        walkResult.append(nextEdge.getTargetNode().getId()).append(" ");
        System.out.println("Current random walk result: " + walkResult.toString());

        // Check if the selected edge has been visited before
        if (visitedEdges.contains(nextEdge.getId())) {
          System.out.println("Cannot continue. Visited previously.");
          lastVisitedNode = nextEdge.getTargetNode().getId();
          break;
        }

        visitedEdges.add(nextEdge.getId());
        currentNode = nextEdge.getTargetNode();

        System.out.println("Current edge: " + nextEdge.getId());
        System.out.println("Do you want to continue? (y/n)");
        String continueChoice = scanner.nextLine().trim();
        if (continueChoice.equalsIgnoreCase("n") || visitedNodes.size() == graph.getNodeCount()) {
          break; // User chooses not to continue or all nodes visited
        }
      }

      // Add the last visited node to the results
      if (lastVisitedNode != null) {
        walkResults.add(lastVisitedNode);
      }

      // Write the results to file
      writer.write(String.join(" ", walkResults));
      writer.write(System.lineSeparator()); // Add newline after each walk

      writer.close();
      System.out.println("Random walk with user choice has been saved to 'random_walk.txt'");
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

    SecureRandom secureRandom = new SecureRandom();

    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("random_walk_result.txt"), StandardCharsets.UTF_8)) {
      for (int i = 0; i < times; i++) {
        org.graphstream.graph.Node currentNode = graph.getNode(secureRandom.nextInt(graph.getNodeCount()));
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
          org.graphstream.graph.Edge nextEdge = edges[secureRandom.nextInt(edges.length)];

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
  public String calculateShortestPath(String startWord, String endWord) {
    StringBuilder result = new StringBuilder();
    if (!graph.getNodeSet().contains(graph.getNode(startWord))) {
      result.append("No such startword in the graph!\n");
      return result.toString();
    }

    // If endWord is empty, compute shortest paths to all other words
    if (endWord.isEmpty()) {
      for (org.graphstream.graph.Node node : graph) {
        if (!node.getId().equals(startWord)) {
          result.append(calculateShortestPath(startWord, node.getId()));
        }
      }
      return result.toString();
    }

    if (!graph.getNodeSet().contains(graph.getNode(endWord))) {
      result.append("No such endword in the graph!\n");
      return result.toString();
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
    // Check if the endWord is reachable
    if (distances.get(endWord) == Integer.MAX_VALUE) {
      result.append("No path from ").append(startWord).append(" to ").append(endWord).append("!\n");
      return result.toString();
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
    result.append("Shortest path from ").append(startWord).append(" to ").append(endWord).append(":\n");
    result.append(String.join(" -> ", shortestPath)).append("\n");
    int pathLength = distances.get(endWord);
    result.append("Path length: ").append(pathLength).append("\n");
    return result.toString();
  }


  Graph graph;

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


  public void readTextFile(String filePath) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
    String content;
    String previousWord = null;


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

    if (graph.getNode(word1) == null && graph.getNode(word2) != null) {
      return "No first word " + word1 +  " in the graph!";
    }
    if (graph.getNode(word1) != null && graph.getNode(word2) == null) {
      return "No second word " + word2 +  " in the graph!";
    }
    if (graph.getNode(word1) == null && graph.getNode(word2) == null) {
      return "No " + word1 + " and " + word2 + " in the graph!";
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
    SecureRandom secureRandom = new SecureRandom();

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
        String bridgeWord = bridgeWords.get(secureRandom.nextInt(bridgeWords.size()));
        newText.append(bridgeWord).append(" ");
      }
    }
    newText.append(words[words.length - 1]); // Add the last word

    return newText.toString();
  }


  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    System.out.print("Enter file path: ");
    String filePath = scanner.nextLine().trim();  // 从用户输入获取文件路径
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
          String out = myGraph.calculateShortestPath(startWord, endWord);
          System.out.println(out);
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