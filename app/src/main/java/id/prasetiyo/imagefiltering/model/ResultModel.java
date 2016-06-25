package id.prasetiyo.imagefiltering.model;

import android.net.Uri;

/**
 * Created by aoktox on 13/06/16.
 */
public class ResultModel implements Comparable<ResultModel>{
    private String img;
    private Double jarak;

    public ResultModel(String img, double jarak) {
        this.img=img;
        this.jarak = jarak;
    }

    public String getImg() {
        return img;
    }

    public Uri getUri() {
        Uri uri = Uri.parse(img);
        return uri;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }

    @Override
    public int compareTo(ResultModel o) {
        return jarak.compareTo(o.jarak);
    }
}
