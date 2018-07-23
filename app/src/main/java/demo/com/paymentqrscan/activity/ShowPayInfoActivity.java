package demo.com.paymentqrscan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import demo.com.paymentqrscan.Bean.Item;
import demo.com.paymentqrscan.utility.ItemAdapter;
import demo.com.paymentqrscan.R;

/**
 * @author : zpf
 * @time : 2018年7月22日
 */
public class ShowPayInfoActivity extends AppCompatActivity {
    List<Item> list = new ArrayList<>();
    ItemAdapter itemAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_info);
        listView = findViewById(R.id.listview2);

        initList();
        //绑定list数据到item布局里显示出来
        itemAdapter = new ItemAdapter(ShowPayInfoActivity.this, R.layout.item, list);
        listView.setAdapter(itemAdapter);

    }

    private void initList() {
        Intent intent = this.getIntent();
        String value = intent.getStringExtra("orderInfo");
        String[] datas = value.split(",");
        for (int i = 0; i < datas.length; i += 6) {
            Item item = new Item(Integer.valueOf(datas[i]), datas[i + 1], datas[i + 2],
                    datas[i + 3], datas[i + 4], datas[i + 5]);
            list.add(item);
        }
    }

}
