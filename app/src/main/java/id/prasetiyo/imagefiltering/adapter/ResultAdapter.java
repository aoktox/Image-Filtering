package id.prasetiyo.imagefiltering.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import id.prasetiyo.imagefiltering.R;
import id.prasetiyo.imagefiltering.model.ResultModel;

/**
 * Created by aoktox on 13/06/16.
 */

public class ResultAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<ResultModel> results;

    public ResultAdapter(Activity activity, ArrayList<ResultModel> results) {
        this.activity = activity;
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public ResultModel getItem(int location) {
        return results.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_result, null);

        ImageView thumnail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView jarak = (TextView) convertView.findViewById(R.id.jarak);

        ResultModel result = results.get(position);
        Glide.with(activity.getApplicationContext()).load(result.getImg()).into(thumnail);
        jarak.setText(""+result.getJarak());
        return convertView;
    }
}
