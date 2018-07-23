package demo.com.paymentqrscan.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author: zpf
 * @time : 2018年7月22日
 * 订单状态选项菜单
 */
public class StateItemDialog extends DialogFragment {
    private  String title ;
    private String [] items;
    private DialogInterface.OnClickListener onClickListener;

    public void show(String title , String [] items , DialogInterface.OnClickListener onClickListener
            , FragmentManager fragmentManager) {
        this.title = title;
        this.items = items;
        this.onClickListener = onClickListener;
        show(fragmentManager,"StateItemsDialog" );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setItems(items, onClickListener);
        return builder.create();
    }

}
