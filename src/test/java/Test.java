import java.util.HashMap;
import java.util.Map;

/**
 * @author yejinbiao
 * @create 2017-02-13-下午12:46
 */

public class Test {
    public static void main(String [] args) {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("1",2);
        hashMap.put("2",2);
        hashMap.put("3",3);
        hashMap.put("4",4);
        hashMap.values().remove(4);
        for (Map.Entry<String,Integer> entry:hashMap.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}
