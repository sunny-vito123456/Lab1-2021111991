package demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;


public class TestMyGraph {
  private MyGraph myGraph;

  @BeforeEach
  public void setUp() throws IOException {
    // 创建一个新的图对象，并手动添加节点和边
    myGraph = new MyGraph("C:\\Users\\Lenovo\\Desktop\\ruanjiangongcheng.txt");
    myGraph.graph = new SingleGraph("Test Graph");
    myGraph.readTextFile("C:\\Users\\Lenovo\\Desktop\\ruanjiangongcheng.txt");
  }


  @Test
  public void testQueryBridgeWords_NoBridgeWord() {
    String result = myGraph.queryBridgeWords("with", "exported");
    assertEquals("No bridge words from \"with\" to \"exported\"", result);
  }

  @Test
  public void testQueryBridgeWords_WithBridgeWord() {
    String result = myGraph.queryBridgeWords("popular", "british");
    assertEquals("The bridge words from \"popular\" to \"british\" is: with.", result);
  }

  @Test
  public void testQueryBridgeWords_Word1NotInGraph() {
    String result = myGraph.queryBridgeWords("foo", "british");
    assertEquals("No first word foo in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_Word2NotInGraph() {
    String result = myGraph.queryBridgeWords("with", "foo");
    assertEquals("No second word foo in the graph!", result);
  }

  @Test
  public void testQueryBridgeWords_BothWordsNotInGraph() {
    String result = myGraph.queryBridgeWords("foo", "bar");
    assertEquals("No foo and bar in the graph!", result);
  }
}
