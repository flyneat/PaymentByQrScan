package demo.com.paymentqrscan.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import demo.com.paymentqrscan.Bean.OrderInfo;
import demo.com.paymentqrscan.activity.CaptureActivity;
import demo.com.paymentqrscan.activity.MainActivity;
import demo.com.paymentqrscan.activity.ShowOrdersActivity;
import demo.com.paymentqrscan.activity.ShowPayInfoActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Tool {
    private static  int mID=0;

    /**
     * 获取OrderInfo数据表 id 主键
     * @return id
     */
    public static int getID(){
        return ++mID;
    }

    /**
     *  获取并处理扫码信息：
     * @param requestCode
     * @param resultCode
     * @param data
     * @return  扫码的内容
     */
    public static  String getQrInfo(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
                return intentResult.getContents();
            }
         else {
            return "取消扫码";
        }
    }

    /**
     * 生成订单交易时间
     * @param date
     * @return  格式化的订单的交易时间
     */
    public static String getDateTime(Date date){
        //获取规定格式的交易时间
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date);
    }

    /**
     * 生成订单号
     * @return 18位数字格式的订单号
     */
    public static String getOrderNum() {
        return getOrderNum();
    }

    /**
     * 生成订单号
     * @param date
     * @return 18位数字格式的订单号
     */
    public static String getOrderNum(Date date){
        Random random = new Random();
        int tailNumber = random.nextInt(9000) + 1000;
        return new SimpleDateFormat("yyyyMMddHHmmss").format(date) + tailNumber;
    }

    /**
     * 开始扫码：可自定义扫码框界面，并做初始化操作
     * @param activity
     */
    public static void scanQR(Activity activity) {
        new IntentIntegrator(activity)
                //设置扫码框方向是否锁定
                .setOrientationLocked(false)
                //设置扫码界面的提示信息
                .setPrompt("请将二维码置于扫码框内")
                // 设置自定义的activity是CaptureActivity
                .setCaptureActivity(CaptureActivity.class)
                //初始化扫描
                .initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }


    /**
     * 发送订单信息到商家后台
     * @param context
     * @param orderInfo 订单数据
     */
    public static void  postOrderInfo(final Context context , final String orderInfo) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("是否发送订单信息到商家后台？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "取消发送", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("提示");
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("正在发送订单信息");
                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //成功付款后,更新订单状态信息,再显示当前订单详情：
                                Toast.makeText(context,"发送成功",Toast.LENGTH_SHORT).show();
                                String [] subString = orderInfo.split(",");
                                /*
                                    额,原先取值 orderNum = subString[5] ,这样错误地取值到state的值
                                    导致更新数据库的条件 orderNum 与 state的指做比较,致使更新数据库失败
                                 */
                                String orderNum = subString[4];
                                OrderInfo order = new OrderInfo();
                                order.setState("完成");
                                order.updateAll("orderNum = ?", orderNum);

                                Intent intent1 = new Intent(context , ShowPayInfoActivity.class);
                                intent1.putExtra("orderInfo", orderInfo.replace("待付款" ,"完成"));
                                context.startActivity(intent1);
                            }
                        });
                        progressDialog.show();
                        // 开启一个子线程，发送订单信息到商家后台
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //post timeout is to be set 5 second
                                    Thread.sleep(2000);
                                    progressDialog.cancel();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }).create().show();
    }

    /**
     * 预保存订单数据到SQLite数据库中
     * @param orderInfo 订单信息
     */
    public static void saveOrderInfo(String orderInfo) {
            Log.d("创建数据库", LitePal.getDatabase().toString());
            OrderInfo orderData = new OrderInfo();
            String[] values = orderInfo.split(",");
            orderData.setId(Integer.valueOf(values[0]));
        /**这里存在一个将存储info 和 sum 字段信息 顺序弄反的bug,导致再查询所有订单页面显示错误
         * 错误地方:
         * orderData.setSum(values[1]);
         * orderData.setInfo(values[2]);
         */
            orderData.setInfo(values[1]);
            orderData.setSum(values[2]);
            orderData.setDate(values[3]);
            orderData.setOrderNum(values[4]);
            orderData.setState(values[5]);
            orderData.save();
    }


    /**
     * 从SQLite数据库读取所有订单信息
     * @return  所有订单信息
     */
    public static String  queryOrders() {
        //查询交易完成的所有订单
        List<OrderInfo> infos = LitePal.findAll(OrderInfo.class);
        //加条件语句，为了程序的健壮性
        if (!infos.isEmpty()) {
            String orderInfos = "";
            for (OrderInfo info : infos) {
                int id = info.getId();
                String data = info.getInfo();
                String sum = info.getSum();
                String dateTime = info.getDate();
                String orderNum = info.getOrderNum();
                String state = info.getState();
                //组装订单信息
                orderInfos = orderInfos + id + "," + data + "," + sum
                        + "," + dateTime + "," + orderNum + "," + state + ",";
            }
            return orderInfos;
        } else {
            return null ;
        }
    }

    /**
     * 在ListView订单页面上显示订单信息
     * @param context
     */
    public static void showOrdersInfo(Context context) {
        String orderInfo = queryOrders();
        Log.d("showOrdersInfo:",orderInfo);
        if(orderInfo != null){
            Intent intent2 = new Intent(context , ShowOrdersActivity.class);
            intent2.putExtra("orderInfo", orderInfo);
            context.startActivity(intent2);
        }else{
            Toast.makeText(context, "数据库中没有完成的订单", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 处理退款事务
     * @param context
     */
    public static void refundAction(final Context context) {
        final EditText orderNumEt = new EditText(context);
        orderNumEt.setHint("请输入18位数字订单号");
        new AlertDialog.Builder(context)
                .setTitle("退款")
                .setView(orderNumEt)
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {
                        String orderNum = orderNumEt.getText().toString().trim();
                        if (refund(orderNum)) {
                            Toast.makeText(context, "退款完成", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle("提示")
                                    .setMessage("订单号不存在或该订单处于待付款状态")
                                    .setNeutralButton("确定", null)
                                    .create()
                                    .show();
                        }
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

    /**
     * 依据订单号找到订单，实现退款
     * @param orderNum : 订单号
     */
    private static boolean refund(String orderNum) {
        List<OrderInfo> orderInfos = LitePal.where("orderNum = ? and state = ?", orderNum,"完成")
                .find(OrderInfo.class);
        if (!orderInfos.isEmpty()) {
            //注意List<>集合索引index是从数字 0 开始的
            OrderInfo orderInfo = orderInfos.get(0);
            orderInfo.setState("已退款");
            orderInfo.save();
            return true;
        } else {
            return false;
        }
    }
}

