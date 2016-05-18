package wfu.com.documentexpress.adapter;

/**
 * Created by Lenovo on 2016/4/20.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.List;

import wfu.com.documentexpress.R;
import wfu.com.documentexpress.utils.ImageLoader;

public class ImageViewAdapter extends BaseAdapter {
    private List<String> image;
    private boolean isChice[];
    private Context context;
    private ImageLoader mImageLoader;

    public ImageViewAdapter(List<String> image,Context context,boolean[] isChice) {
        Log.i("hck", image.size()+"lenght");
        this.isChice = isChice;
        this.image = image;
        this.context = context;
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    }

    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public Object getItem(int arg0) {
        return image.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View view = arg1;
        GetView getView=null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.imageitem, null);
            getView = new GetView();
            getView.imageView=(ImageView) view.findViewById(R.id.image_item);
            getView.checkBox = (CheckBox) view.findViewById(R.id.img_checked);
            view.setTag(getView);
        } else {
            getView = (GetView) view.getTag();
        }
        getView.imageView.setImageResource(R.drawable.pictures_no);
        mImageLoader.loadImage(image.get(arg0), getView.imageView, false);
        if(isChice[arg0]==true){
            getView.checkBox.setChecked(true);
        }else{
            getView.checkBox.setChecked(false);
        }
        return view;
    }

    static class GetView {
        ImageView imageView;
        CheckBox checkBox;
    }


//    //主要就是下面的代码了
//    private LayerDrawable getView(int post) {
//        Bitmap bitmap = ;
//        Bitmap bitmap2=null;
//        LayerDrawable la=null;
//        if (isChice[post]== true){
//            bitmap2 = BitmapFactory.decodeResource(context.getResources(),
//                    R.drawable.check_yes);
//        }
//        if (bitmap2!=null) {
//            Drawable[] array = new Drawable[2];
//            array[0] = new BitmapDrawable(bitmap);
//            array[1] = new BitmapDrawable(bitmap2);
//            la= new LayerDrawable(array);
//            la.setLayerInset(0, 0, 0, 0, 0);   //第几张图离各边的间距
//            la.setLayerInset(1, 0, 150, 150, 0);
//        }
//        else {
//            Drawable[] array = new Drawable[1];
//            array[0] = new BitmapDrawable(bitmap);
//            la= new LayerDrawable(array);
//            la.setLayerInset(0, 0, 0, 0, 0);
//        }
//        return la; // 返回叠加后的图
//    }
    public void chiceState(int post)
    {
        isChice[post]=isChice[post]==true?false:true;
    }
}

