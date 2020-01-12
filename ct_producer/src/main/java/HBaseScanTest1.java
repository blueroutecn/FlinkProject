
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HBaseScanTest1 {
    public static void main(String[] args) throws IOException {
        scanTest();
    }


    private static Configuration conf = null;

    static{
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node01");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
    }


    public static void scanTest() throws IOException {
        HTable hTable = new HTable(conf, "test:calllog");
        Scan scan = new Scan();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startTimePoint = null;
        String endTimePoint = null;
        try {
            startTimePoint = String.valueOf(simpleDateFormat.parse("2017-01-1").getTime());
            endTimePoint = String.valueOf(simpleDateFormat.parse("2017-12-01").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Filter filter1 = HBaseFilterUtil.gteqFilter("f1", "date_time_ts", Bytes.toBytes(startTimePoint));
        Filter filter2 = HBaseFilterUtil.ltFilter("f1", "date_time_ts", Bytes.toBytes(endTimePoint));
        Filter filterList = HBaseFilterUtil.andFilter(filter1, filter2);
        scan.setFilter(filterList);

        ResultScanner resultScanner = hTable.getScanner(scan);
        //每一个rowkey对应一个result
        for(Result result : resultScanner){
            //每一个rowkey里面包含多个cell
            Cell[] cells = result.rawCells();
            for(Cell c: cells){
                System.out.print("行：" + Bytes.toString(CellUtil.cloneRow(c)));
                System.out.print("；列族：" + Bytes.toString(CellUtil.cloneFamily(c)));
                System.out.print("；列：" + Bytes.toString(CellUtil.cloneQualifier(c)));
                System.out.println("；值：" + Bytes.toString(CellUtil.cloneValue(c)));
//                System.out.println(Bytes.toString(CellUtil.cloneRow(c))
//                        + ","
//                        + Bytes.toString(CellUtil.cloneFamily(c))
//                        + ":"
//                        + Bytes.toString(CellUtil.cloneQualifier(c))
//                        + ","
//                        + Bytes.toString(CellUtil.cloneValue(c)));
            }
        }
    }
}
