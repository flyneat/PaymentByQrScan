package demo.com.paymentqrscan.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import demo.com.paymentqrscan.Bean.Item;
import demo.com.paymentqrscan.R;

/**
 * Created by zpf on 7/18/18.
 */

public class ItemAdapter extends ArrayAdapter<Item> {
    private int layoutId;


    public ItemAdapter(Context context, int layoutId, List<Item> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        //使用一个内部类Viewholder来存储item.xml控件上的组件对象，复用这些对象，减小内存开销
        ViewHolder viewHolder = new ViewHolder();
        View view ;
        //复用convertView，减少内存开销
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

            viewHolder.mIdTv = view.findViewById(R.id.text_id);
            viewHolder.mInfoTv = view.findViewById(R.id.text_info);
            viewHolder.mSumTv = view.findViewById(R.id.text_sum);
            viewHolder.mTimeTv = view.findViewById(R.id.text_time);
            viewHolder.mOrderNumTv = view.findViewById(R.id.text_orderNum);
            viewHolder.mStateTv = view.findViewById(R.id.text_state);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mIdTv.setText(String.valueOf(item.getId()));
        viewHolder.mInfoTv.setText(item.getInfo());
        viewHolder.mSumTv.setText(item.getSum());
        viewHolder.mTimeTv.setText(item.getTime());
        viewHolder.mOrderNumTv.setText(item.getOrderNum());
        viewHolder.mStateTv.setText(item.getState());

        return view;
    }

    class ViewHolder{
        TextView mIdTv;
        TextView mInfoTv;
        TextView mSumTv;
        TextView mTimeTv;
        TextView mOrderNumTv;
        TextView mStateTv;
    }

}

