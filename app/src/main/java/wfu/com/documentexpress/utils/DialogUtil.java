package wfu.com.documentexpress.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Lenovo on 2016/5/24.
 */
public class DialogUtil {
   public static void showDialog(final Activity context,String msg,DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context).setTitle("注意！")//设置对话框标题
                .setMessage(msg)//设置显示的内容
                .setPositiveButton("确定", okListener).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                dialog.dismiss();
            }
        }).show();//在按键响应事件中显示此对话框
    }
}
