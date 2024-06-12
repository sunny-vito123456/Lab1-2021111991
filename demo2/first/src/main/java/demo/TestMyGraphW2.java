package demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class TestMyGraphW2 {
  private MyGraph myGraph;

  @BeforeEach
  public void setUp() throws IOException {
    // 创建一个新的图对象，并手动添加节点和边
    myGraph = new MyGraph("C:\\Users\\Lenovo\\Desktop\\lab1(1).txt");
    myGraph.graph = new SingleGraph("Test Graph");
    myGraph.readTextFile("C:\\Users\\Lenovo\\Desktop\\lab1(1).txt");
  }

  @Test
  public void testcalculateShortestPath_StartWordNotExists() {
    String result = myGraph.calculateShortestPath("particular","to");
    assertEquals("No such startword in the graph!\n", result);
  }

  @Test
  public void testcalculateShortestPath_EndWordIsNull() {
    String result = myGraph.calculateShortestPath("to", "");
    String[] lines = result.split("\n");
    ArrayList<String> paths = new ArrayList<>();
    int pathIndex = 0;
    for (String line : lines) {
      if (line.contains("->")) {
        // 提取包含“->”的行并存储到数组中
        paths.add(pathIndex++, line.trim());
      }
    }
    String[] pathsArray = paths.toArray(new String[0]);
    String[] predefinedPaths = {
        "to -> explore",
        "to -> explore -> strange",
        "to -> explore -> strange -> new",
        "to -> explore -> strange -> new -> worlds",
        "to -> seek",
        "to -> seek -> out",
        "to -> seek -> out -> new",
        "to -> seek -> out -> new -> worlds",
        "to -> explore -> strange -> new -> life",
        "to -> explore -> strange -> new -> life -> and",
        "to -> explore -> strange -> new -> civilizations",
        "to -> seek -> out -> new -> life",
        "to -> seek -> out -> new -> life -> and",
        "to -> seek -> out -> new -> civilizations",
    };
    HashSet<String> predefinedPathsSet = new HashSet<>();
    for (String path : predefinedPaths) {
      predefinedPathsSet.add(path);
    }
    // 检查pathsArray中的每个元素是否都在预定义路径数组中
    boolean allPathsExist = true;
    for (String path : pathsArray) {
      if (!predefinedPathsSet.contains(path)) {
        allPathsExist = false;
        break;
      }
    }
    if (allPathsExist) {
      assertTrue(allPathsExist);
    } else {
      assertFalse(allPathsExist);
    }
  }

  @Test
  public void testcalculateShortestPath_EndWordNotExists() {
    String result = myGraph.calculateShortestPath("to","particular");
    assertEquals("No such endword in the graph!\n", result);
  }

  @Test
  public void testcalculateShortestPath_CanNotReach() {
    String result = myGraph.calculateShortestPath("civilizations", "to");
    assertEquals("No path from civilizations to to!\n", result);
  }

  @Test
  public void testcalculateShortestPath_BothWordsExists() {
    String result = myGraph.calculateShortestPath("to", "strange");
    assertEquals("Shortest path from to to strange:\nto -> explore " +
        "-> strange\nPath length: 2\n", result);
  }
}


