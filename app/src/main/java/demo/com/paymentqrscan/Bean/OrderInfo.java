package demo.com.paymentqrscan.Bean;

import org.litepal.crud.LitePalSupport;

/**
 * @author zpf
 */
public class OrderInfo extends LitePalSupport {
    //订单编号
    private int id ;

    //支付金额
    private String sum;

    //扫码信息
    private String info;

    //交易时间
    private String date;

    //订单号
    private String orderNum;

    //交易状态
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String data) {
        this.date = data;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
