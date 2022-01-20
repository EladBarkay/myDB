import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
public class  ColumnData {
    private String columnName;
    private Class<?> type;
    private HashMap<Object, Object> data;

    public ColumnData(String columnName, Class<?> type, HashMap<Object, Object> data) {
        this.columnName = columnName;
        this.type = type;
        this.data.putAll(data);
    }

    public ColumnData(String columnName, Class<?> type){
        this.columnName = columnName;
        this.type = type;
        this.data = new HashMap<>();
    }

    /**
     * java.lang.Integer
     * 0:4
     * 1:98
     * 2:1
     * 3:53
     * 4:54
     * 5:77
     * 6:15
     * 7:23
     * 8:42
     * 9:53
     * 15:90
     */
    public ColumnData(String name, Class<?> keyType, String columnString){
        this.columnName = name;
        String[] values = columnString.split("\n");
        try {
            this.type = Class.forName(values[0]);
        } catch (ClassNotFoundException e) {
            this.type = Object.class;
            System.out.println(e.getMessage());
        }

        for (int i = 1; i < values.length; i++) {
            Object key = keyType.cast(values[i].split(":")[0]);
            Object value = type.cast(values[i].split(":")[1]);
            this.data.put(key, value);
        }
    }

    public void put(Object key, Object value){
        this.data.put(key, value);
    }

    public void remove(Object key){
        this.data.remove(key);
    }

    public Object get(Object key){
        return this.data.get(key);
    }

    public void replace(Object key, Object value){
        this.data.replace(key, value);
    }

    public Set<Object> keySet(){
        return this.data.keySet();
    }

    public boolean containsKey(Object key){
        return this.data.containsKey(key);
    }

    public String stringToWrite() {
        StringBuilder builder = new StringBuilder();
        builder.append(getType().getName()).append("\n");
        for (Object key : data.keySet()) {
            builder.append(key).append(":").append(data.get(key)).append("\n");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
