import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        DB db = new DB();
        try {
            db.load("src\\main\\resources\\db.zip");
        } catch (IOException e) {
            System.out.println("nt");
            e.printStackTrace();
        }
    }
    public static DB testDBMaker(){
        ArrayList<ColumnData> data = new ArrayList<>();
        data.add(new  ColumnData("id", Integer.class));
        for (int i = 0; i < 10; i++) {
            data.get(0).put(i, new Random().nextInt(0, 100));
        }
        data.add(new ColumnData("name", String.class));
        for (int i = 0; i < 10; i++) {
            data.get(1).put(i, "randomName" + new Random().nextInt(0, 100));
        }
        data.add(new ColumnData("money", Double.class));
        for (int i = 0; i < 10; i++) {
            data.get(2).put(i, new Random().nextDouble(0, 10));
        }
        Table table1 = new Table("tempTable1", new ArrayList<>(data.get(0).keySet()), data.toArray(ColumnData[]::new));
        table1.addRecord(new Record(15, new ArrayList<>(){{
            add(90);
            add("Elad");
            add(8.26514);
        }}));


        ArrayList<ColumnData> data2 = new ArrayList<>();
        data2.add(new  ColumnData("id", Integer.class));
        for (int i = 0; i < 10; i++) {
            data2.get(0).put(i, new Random().nextInt(0, 100));
        }
        data2.add(new ColumnData("name", String.class));
        for (int i = 0; i < 10; i++) {
            data2.get(1).put(i, "randomName" + new Random().nextInt(0, 100));
        }
        data2.add(new ColumnData("money", Double.class));
        for (int i = 0; i < 10; i++) {
            data2.get(2).put(i, new Random().nextDouble(0, 10));
        }
        Table table2 = new Table("tempTable2", new ArrayList<>(data2.get(0).keySet()), data2.toArray(ColumnData[]::new));
        table2.addRecord(new Record(50, new ArrayList<>(){{
            add(19);
            add("Ofri");
            add(5.36297841);
        }}));


        DB db = new DB();
        db.setTables(new HashMap<>(){{put(table1.getName(), table1); put(table2.getName(), table2);}});
        return db;
    }
}
