package com.example.staj;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

public class ViewPagerAdapter extends PagerAdapter {
    // Slider adapter
    private Context context;
    private LayoutInflater layoutInflater;
    private String[] urls;
    private String[] titles;
    private String[] links;


    public ViewPagerAdapter(Context context, String[] urls, String[] titles, String[] links){
        this.context = context;
        this.urls = urls;
        this.titles = titles;
        this.links = links;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){ return urls.length; }

    @Override
    public boolean isViewFromObject(View view, Object object ){ return view.equals(object); }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        View imageLayout = layoutInflater.inflate(R.layout.slider, container, false);
        assert imageLayout != null;
        final ImageView imageView =  imageLayout.findViewById(R.id.sliderImage);
        final TextView textView = imageLayout.findViewById(R.id.sliderText);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( links[position].length() > 0 && !links[position].equals("") && !links[position].isEmpty()){
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(links[position])));
                }else Toast.makeText(context,"Link bulunmuyor",Toast.LENGTH_LONG).show();
            }
        });

        textView.setText(titles[position]);
        Glide.with(context).load(urls[position]).into(imageView);
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){ container.removeView((View) object); }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader){}

    @Override
    public Parcelable saveState(){return null;}
}
