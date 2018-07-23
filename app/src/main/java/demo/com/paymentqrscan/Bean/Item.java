package demo.com.paymentqrscan.Bean;

/**
 * Created by zpf on 7/18/18.
 */
public class Item {
    private int id;
    private String info;
    private String sum;
    private String time;
    private String orderNum;
    private String state;

    public Item(int id, String info, String sum, String time,
                String orderNum , String state) {
        this.id = id;
        this.info = info;
        this.sum = sum;
        this.time = time;
        this.orderNum = orderNum;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public String getSum() {
        return sum;
    }

    public String getTime() {
        return time;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getState() {
        return state;
    }
}