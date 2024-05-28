// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String[] otherArgs = new String[10];
        otherArgs[0] = "hdfs://localhost:9000/input/experiment2/聚类数据.txt";
        otherArgs[1] = "hdfs://localhost:9000/output/experiment2/GMM/Means";
        otherArgs[2] = "hdfs://localhost:9000/output/experiment2/GMM/Covariances";
        otherArgs[3] = "hdfs://localhost:9000/output/experiment2/GMM/OUTPUT";
        System.out.println(otherArgs[0]);
        System.out.println(otherArgs[1]);
        System.out.println(otherArgs[2]);
        System.out.println(otherArgs[3]);
    }
}