package demo.com.paymentqrscan.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import demo.com.paymentqrscan.R;
import demo.com.paymentqrscan.utility.Tool;

/* author:zpf
 * date : 2018.7.16
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mInputMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取页面上各组件的实例
        Button mScan = findViewById(R.id.payBtn);
        Button mQueryOrder = findViewById(R.id.selectBtn);
        //Button mRefundBtn = findViewById(R.id.refundBtn);
        mInputMoney = findViewById(R.id.inputMoney);

        //为按钮添加点击事件
        mScan.setOnClickListener(this);
        mQueryOrder.setOnClickListener(this);
        //mRefundBtn.setOnClickListener(this);
    }

    //获取二维码信息,信息通过intentResult保存和传递

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取二维码内容
        String QrInfo = Tool.getQrInfo(requestCode, resultCode, data);
        if (QrInfo == null) {
            Toast.makeText(this, "取消扫码", Toast.LENGTH_SHORT).show();
        } else {
            if ("".equals(QrInfo)) {
                Toast.makeText(this, "无内容", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this , "扫码内容：\n" + QrInfo , Toast.LENGTH_LONG).show();
                //获取订单ID
                int id = Tool.getID();

                //获取订单支付金额
                String sum = mInputMoney.getText().toString();

                //获取当前系统时间
                Date date = new Date(System.currentTimeMillis());

                //获取订单交易时间
                String dateTime = Tool.getDateTime(date);

                //获取订单号
                String orderNum = Tool.getOrderNum(date);

                //组装订单信息
                String orderInfo = id + "," + QrInfo + "," + sum + "," + dateTime + ","
                                   + orderNum + "," + "待付款";

                //预保存订单数据到SQLite数据库
                Tool.saveOrderInfo(orderInfo);

                //发送订单信息到商家后台
                Tool.postOrderInfo(MainActivity.this,orderInfo);
            }
        }

    }

    //响应按钮点击事件

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //扫码支付
            case R.id.payBtn:
                Tool.scanQR(this);
                break;

            //查询所有订单
            case R.id.selectBtn:
                    Tool.showOrdersInfo(MainActivity.this);
                break;
            default:
                break;
        }
    }

}
