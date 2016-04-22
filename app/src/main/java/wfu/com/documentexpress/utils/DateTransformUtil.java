package wfu.com.documentexpress.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lenovo on 2016/4/15.
 */
public class DateTransformUtil {
    public static String getModifyTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date(time);
        String nowdate = sdf.format(date);
        String[] temp = nowdate.split("-");
        if(temp[1].charAt(0)=='0'){
            temp[1] = temp[1].substring(1);
        }
        String formatdate = temp[0]+"年"+temp[1]+"月"+temp[2]+"日";
        return formatdate;
    }

}
