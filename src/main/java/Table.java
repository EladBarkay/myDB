import lombok.Data;
import lombok.SneakyThrows;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

@Data
public class Table {
    String name;
    ArrayList<Object> keys;
    ColumnData[] columns;

    public Table(String name, ArrayList<Object> keys, ColumnData[] columns) {
        this.name = name;
        this.keys = keys;
        this.columns = columns;
    }

    public Table(String name, Properties tableColumnsAndTypes){
        this.name = name;
        this.keys = new ArrayList<>();
        ArrayList<ColumnData> tempColumns = new ArrayList<>();
        for (Object o : tableColumnsAndTypes.keySet()) {
            tempColumns.add(new ColumnData((String)o, o.getClass(), new LinkedHashMap<>()));
        }
        this.columns = tempColumns.toArray(ColumnData[]::new);
    }

    /**
     * create table from string data when reading a file
     *
     * java.lang.Integer
     * 0,1,2,3,4,5,6,7,8,9,15
     * 3
     */
    @SneakyThrows
    public Table(String name, String writeString){
        //variables[0] = key type, variables[1] = keys, variables[2] = columns count
        String[] variables = writeString.split("\n");
        if (variables.length != 3)
            throw new InvalidTargetObjectTypeException("String is incompatible " +
                    "with table creating");

        this.name = name;
        Class<?> type = Class.forName(variables[0]);
        for (String key : variables[1].split(",")) {
            this.keys.add(type.cast(key));
        }
        this.columns = new ColumnData[Integer.parseInt(variables[2])];
    }


    public void addRecord(Record record){
        recordValidationCheck(record, false);
        keys.add(record.getKey());
        for (int i = 0; i < columns.length; i++) {
            columns[i].put(record.getKey(), record.getValues().get(i));
        }
    }

    @SneakyThrows
    public void removeRecord(Record record){
        recordValidationCheck(record, true);
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].get(record.getKey()) != record.getValues().get(i)){
                throw new RecordsNotMatchException("Records don't match: different " +
                        "values (column " + columns[i].getColumnName() + ": " +
                        columns[i].get(record.getKey()) +
                        ", record " + record.getKey() +  ": " +
                        record.getValues().get(i) + ")");
            }
        }
        keys.remove(record.getKey());
        for (ColumnData column : columns) {
            column.remove(record.getKey());
        }
    }

    public void updateRecord(Record record){
        recordValidationCheck(record, true);
        for (int i = 0; i < columns.length; i++) {
            columns[i].replace(record.getKey(), record.getValues().get(i));
        }
    }

    @SneakyThrows
    public void recordValidationCheck(Record record, boolean shouldExist){
        if (!keys.isEmpty() && keys.get(0).getClass() != record.getKey().getClass())
            throw new RecordsNotMatchException("Cant add " +
                    record.getKey().getClass().getName() + " key to a table with " +
                    keys.get(0).getClass().getName() + " type keys");
        if (keys.contains(record.getKey()) != shouldExist)
            if (!shouldExist)
                throw new KeyAlreadyExistsException("There is already a record" +
                        " with this key in the table");
            else
                throw new KeyAlreadyExistsException("There is no record" +
                        " with this key in the table");
        if (record.getValues().size() != columns.length)
            throw new InvalidTargetObjectTypeException("Record len ("
                    + record.getValues().size() + ") is invalid for this table len ("
                    + columns.length + ")");
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getType() != record.getValues().get(i).getClass())
                throw new InvalidTargetObjectTypeException("At " +
                        columns[i].getColumnName() + " column," +
                        " the table type (" + columns[i].getType().getName() + ") " +
                        "is different than (" +
                        record.getValues().get(i).getClass().getName() + ")");
        }
    }

    public HashMap<String, String> toWrite(){
        HashMap<String, String> pathDataMap = new HashMap<>();
        for (ColumnData column : columns){
            pathDataMap.put(column.getColumnName() + ".col", column.stringToWrite());
        }
        pathDataMap.put(getName() + ".md", tableToWriteString());
        return pathDataMap;
    }

    private String tableToWriteString(){
        StringBuilder toRet = new StringBuilder();
        if (keys.size() != 0)
            toRet.append(keys.get(0).getClass().getName());
        toRet.append("\n");
        keys.forEach(x-> toRet.append(x).append(","));
        toRet.deleteCharAt(toRet.length()-1);
        toRet.append("\n").append(columns.length);
        return toRet.toString();
    }

}
