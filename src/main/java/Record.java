import lombok.Data;
import java.util.ArrayList;

@Data
public class Record {
    private Object key;
    private ArrayList<Object> values;

    public Record(Object key, ArrayList<Object> values) {
        this.key = key;
        this.values = values;
    }
}
