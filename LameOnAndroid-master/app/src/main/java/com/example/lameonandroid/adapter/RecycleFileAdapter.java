package com.example.lameonandroid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lameonandroid.R;
import com.example.lameonandroid.activity.SdFile;

import java.util.List;

/**
 * Author:pdm on 2016/8/23 10:38
 * Email:aiyh0202@163.com
 */
public class RecycleFileAdapter extends RecyclerView.Adapter<RecycleFileAdapter.SDFileViewHolder> {

    private List<SdFile> data;
    private Context context;

    public RecycleFileAdapter(List<SdFile> data) {
        this.data = data;
    }

    @Override
    public SDFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        //这里必须这样写，item布局的效果才能体现
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
        SDFileViewHolder viewHolder = new SDFileViewHolder(view);
        return viewHolder;
    }

    //这里设置item的类型
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private boolean ischeckd = false;

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    private boolean isEdit = false;

    @Override
    public void onBindViewHolder(final SDFileViewHolder viewHolder, final int position) {
        SdFile sdFile = data.get(position);
        viewHolder.iv_check.setImageResource(R.drawable.cb_unchecked);
        viewHolder.file_img.setImageResource(R.drawable.music);
        viewHolder.file_name.setText(sdFile.getName());
        if (isEdit()) {
            viewHolder.iv_check.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_check.setVisibility(View.GONE);
        }
        float size = (float) sdFile.getSize() / 1024 / 1024;
        //保留一位小数
//        BigDecimal b  =  new BigDecimal(size);
//        float   f  = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        float f = (float) (Math.round(size * 10)) / 10;
        viewHolder.file_size.setText(f + "MB");
//        viewHolder.itemView.setTag(position + "");
        if (mOnItemClickListener != null) {
            if (!viewHolder.itemView.hasOnClickListeners()) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取item的位置，不论是否刷新ViewHolder
                        int position = viewHolder.getLayoutPosition();
                        Toast.makeText(context, "编号 : " + position, Toast.LENGTH_SHORT).show();
//                        int position = Integer.parseInt(viewHolder.itemView.getTag().toString().trim());
                        if (!isEdit() && position >= 0) {
                            mOnItemClickListener.onItemClick(v, position);
                        } else {
                            ischeckd = data.get(position).ischecked();
                            if (ischeckd) {
                                viewHolder.iv_check.setImageResource(R.drawable.cb_unchecked);
                                data.get(position).setIschecked(false);
                            } else {
                                viewHolder.iv_check.setImageResource(R.drawable.cb_checked);
                                data.get(position).setIschecked(true);
                            }
                        }
                        Log.e("编号 ：", position + "");
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        if (!isEdit()){
//                            mOnItemClickListener.onItemLongClick(v, position -1);
//                            return true;
//                        }
//                        return false;
                        return false;
                    }
                });
            }
        }
    }

    /**
     * 80      * 向指定位置添加元素
     * 81      * @param position
     * 82      * @param value
     * 83
     */
    public void add(int position, SdFile value) {
        if (position > data.size()) {
            position = data.size();
        }
        if (position < 0) {
            position = 0;
        }
        data.add(position, value);
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        notifyItemInserted(position);
    }

    /**
     * 移除指定位置元素
     *
     * @param position
     * @return
     */
    public void remove(int position) {
        data.remove(position);
        //这里+1是因为下拉刷新的位置占了一个item，要排除掉
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SDFileViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_check;
        public ImageView file_img;
        public TextView file_name;
        public TextView file_size;

        public SDFileViewHolder(View itemView) {
            super(itemView);
            iv_check = (ImageView) itemView.findViewById(R.id.iv_check);
            file_img = (ImageView) itemView.findViewById(R.id.file_img);
            file_name = (TextView) itemView.findViewById(R.id.file_name);
            file_size = (TextView) itemView.findViewById(R.id.file_size);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;

    /**
     * 119      * 处理item的点击事件和长按事件
     * 120
     */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

//    private class MyTask extends AsyncTask<String, Void, Bitmap> {
//        //启动线程在后台加载
//        private Bitmap bitmap;
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            String path = params[0];//图片路劲
//            int position = Integer.valueOf(params[1]);//文件下标
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;//图片缩小到1/4
//            bitmap = BitmapFactory.decodeFile(path, options);//通过路径加载的图片
//            data.get(position).setBitmap(bitmap);//设置图片
//            return bitmap;
//        }
//
//        //加载完毕后提供刷新UI
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            // TODO Auto-generated method stub
//            RecycleFileAdapter.this.notifyDataSetChanged();
//            super.onPostExecute(result);
//        }
//    }
}
