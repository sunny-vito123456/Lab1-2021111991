package demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;


public class TestMyGraphW {
  private MyGraph myGraph;

  @BeforeEach
  public void setUp() throws IOException {
    // 创建一个新的图对象，并手动添加节点和边
    myGraph = new MyGraph("C:\\Users\\Lenovo\\Desktop\\ruanjiangongcheng.txt");
    myGraph.graph = new SingleGraph("Test Graph");
    myGraph.readTextFile("C:\\Users\\Lenovo\\Desktop\\ruanjiangongcheng.txt");
  }


  @Test
  public void testcalculateShortestPath_BothWordsExists() {
    String result = myGraph.calculateShortestPath("later", "exported");
    assertEquals("Shortest path from later to exported:\nlater -> became -> " +
        "commonly -> exported\nPath length: 3\n", result);
  }

  @Test
  public void testcalculateShortestPath_StartWordNotExists() {
    String result = myGraph.calculateShortestPath("particular", "exported");
    assertEquals("No such startword in the graph!\n", result);
  }

  @Test
  public void testcalculateShortestPath_EndWordNotExists() {
    String result = myGraph.calculateShortestPath("later", "particular");
    assertEquals("No such endword in the graph!\n", result);
  }
}

