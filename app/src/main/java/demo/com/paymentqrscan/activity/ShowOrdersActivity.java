package demo.com.paymentqrscan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import demo.com.paymentqrscan.Bean.Item;
import demo.com.paymentqrscan.Bean.OrderInfo;
import demo.com.paymentqrscan.R;
import demo.com.paymentqrscan.utility.ItemAdapter;
import demo.com.paymentqrscan.utility.StateItemDialog;
import demo.com.paymentqrscan.utility.Tool;

/**
 * @author zpf
 * @time 2018年7月22日
 */
public class ShowOrdersActivity extends AppCompatActivity implements View.OnClickListener {
    List<Item> list = new ArrayList<>();
    ItemAdapter itemAdapter;
    private ListView listView;
    Button sort ;
    Button waitPay;
    Button refundBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_info);
        sort = findViewById(R.id.btn_sort);
        waitPay = findViewById(R.id.btn_waitPay);
        refundBtn = findViewById(R.id.btn_refund);

        listView = findViewById(R.id.listview1);

        sort.setOnClickListener(this);
        waitPay.setOnClickListener(this);
        refundBtn.setOnClickListener(this);

        initList();
        bindList2ItemLayout(list);

        //长按点击事件：
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                final View v = view;
                new AlertDialog.Builder(ShowOrdersActivity.this)
                        .setTitle("提示")
                        .setMessage("是否删除当前订单")
                        .setCancelable(true)
                        .setNegativeButton("否", null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Item item = list.get(position);
                                LitePal.deleteAll(OrderInfo.class, "id =" + item.getId());
                                //重新加载数据到list
                                list.remove(position);
                                bindList2ItemLayout(list);
                                Toast.makeText(ShowOrdersActivity.this, "tip:成功删除", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create().show();
                return true;
            }
        });
    }

    private void initList() {
        Intent intent = this.getIntent();
        String value = intent.getStringExtra("orderInfo");
        Log.d("value is:" , value);
        String[] datas = value.split(",");
        for (int i = 0; i < datas.length; i += 6) {
            Item item = new Item(Integer.valueOf(datas[i]), datas[i + 1], datas[i + 2],
                    datas[i + 3], datas[i + 4], datas[i + 5]);
            list.add(item);
        }
    }


    //绑定list数据到item布局里显示出来
    private void bindList2ItemLayout(List list) {
        itemAdapter = new ItemAdapter(ShowOrdersActivity.this, R.layout.item, list);
        listView.setAdapter(itemAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sort:
                sortAction();
                break;
            case R.id.btn_waitPay:
                restorePayAction();
                break;
            case R.id.btn_refund:
                Tool.refundAction(ShowOrdersActivity.this);
                //实现下拉刷新功能
            default:
                break;
        }
    }

    //依据订单支付状态，对订单进行分类查询
    //显示查询订单状态选项
    private void sortAction() {
        //新建一个订单状态选项菜单
        StateItemDialog itemDialog = new StateItemDialog();
        String[] items = {"完成", "待付款", "已退款"};
        itemDialog.show("订单状态", items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        sortByState("完成");
                        break;
                    case 1:
                        sortByState("待付款");
                        break;
                    case 2:
                        sortByState("已退款");
                        break;
                    default:
                        break;
                }
            }
        }, getFragmentManager());
    }

    private void sortByState(String state) {
        List<Item> listForSortation = new ArrayList<>();
        List<OrderInfo> infos = LitePal.where("state = ?", state).find(OrderInfo.class);
        for (OrderInfo info : infos) {
            Item item = new Item(info.getId(), info.getInfo(), info.getSum()
                    , info.getDate(), info.getOrderNum(), info.getState());
            listForSortation.add(item);
        }
        bindList2ItemLayout(listForSortation);
    }

    //通关订单号找到订单状态为待付款的订单,对其进行恢复支付事务
    private void restorePayAction() {
        final EditText orderNumEt = new EditText(ShowOrdersActivity.this);
        orderNumEt.setHint("输入18位数字形式的订单号");

        //为输入订单号的控件的输入设置制指定为数字类型
        //orderNumEt.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);

        new AlertDialog.Builder(ShowOrdersActivity.this)
                .setTitle("恢复支付")
                .setView(orderNumEt)
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {
                        //获取订单号
                        /**
                         * 此处存在Bug ,不能显示出付款完成的订单信息
                         * 而是新建了一项默认值的订单信息
                         */
                        String orderNum = orderNumEt.getText().toString().trim();
                        List<OrderInfo> list =  LitePal.where("orderNum = ?", orderNum)
                                                .find(OrderInfo.class);
                        if (!list.isEmpty()) {
                            OrderInfo orderInfo = list.get(0);
                            String state = orderInfo.getState();
                            if (!state.equals("待付款")) {
                                Toast.makeText(ShowOrdersActivity.this, "订单完成或已退款", Toast.LENGTH_SHORT)
                                        .show();
                            }else{
                                orderInfo.setState("完成");
                                orderInfo.save();
                                Toast.makeText(ShowOrdersActivity.this, "付款完成", Toast.LENGTH_SHORT)
                                        .show();
                                //显示本次付款完成的订单详情
                                String orderData = orderInfo.getId() + "," + orderInfo.getInfo() + "," + orderInfo.getSum()
                                                    + "," + orderInfo.getDate() + "," + orderInfo.getOrderNum() + "," + "完成";
                                Intent intent = new Intent(ShowOrdersActivity.this, ShowPayInfoActivity.class);
                                intent.putExtra("orderInfo" ,orderData ) ;
                                startActivity(intent);
                            }
                        } else {
                            new AlertDialog.Builder(ShowOrdersActivity.this)
                                    .setTitle("提示")
                                    .setMessage("订单号不存在")
                                    .setNeutralButton("确定", null)
                                    .create()
                                    .show();
                        }
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

}
