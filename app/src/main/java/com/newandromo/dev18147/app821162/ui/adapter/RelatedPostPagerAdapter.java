package com.newandromo.dev18147.app821162.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.util.List;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;

public class RelatedPostPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<EntryEntity> mEntries;
    private RelatedPostListener mListener;

    public interface RelatedPostListener {
        void onSelected(EntryEntity entry);
    }

    public RelatedPostPagerAdapter(Context context, List<EntryEntity> entries, RelatedPostListener listener) {
        this.mContext = context;
        this.mEntries = entries;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return mEntries == null || mEntries.isEmpty() ? 0 : mEntries.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {
        EntryEntity entry = mEntries.get(position);

        int layout = R.layout.list_item_carousel_related_post;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        ViewGroup rootView = view.findViewById(R.id.viewRoot);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView titleTV = view.findViewById(R.id.titleTextView);
        TextView dateTV = view.findViewById(R.id.dateTextView);

        int placeHolderColor = AppUtils.getPlaceholderColor(mContext);
        String date = entry.getDate();
        long dateMillis = entry.getDateMillis();

        try {
            if (dateMillis != 0) {
                CharSequence prettyTime = DateUtils.getRelativeTimeSpanString(
                        dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                if (!TextUtils.isEmpty(prettyTime))
                    date = prettyTime.toString();
            } else {
                String parsedDate = MyDateUtils.parseTimestampToString(date);
                if (!TextUtils.isEmpty(parsedDate)) date = parsedDate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            titleTV.setText(Jsoup.parse(entry.getTitle(), "", Parser.htmlParser()).text());
        } catch (Exception e) {
            titleTV.setText(entry.getTitle());
            e.printStackTrace();
        }

        dateTV.setText(date);

        try {
            Picasso.get()
                    .load(entry.getThumbUrl())
                    .placeholder(placeHolderColor)
                    .error(placeHolderColor)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        rootView.setOnClickListener(view1 -> mListener.onSelected(entry));
        parent.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((CardView) object);
    }

    /*private String[] mImages = new String[]{
            "https://drscdn.500px.org/photo/127870627/h%3D600_k%3D1_a%3D1/df562860314d42dd9a4f8bf4ee0ac0e5",
            "https://drscdn.500px.org/photo/127900863/h%3D600_k%3D1_a%3D1/e63c59888014392bac32cfb9c383bb9e",
            "https://drscdn.500px.org/photo/127891921/h%3D600_k%3D1_a%3D1/c5aec47c6c924d733f58cec483dc41a6",
            "https://drscdn.500px.org/photo/127883901/h%3D600_k%3D1_a%3D1/1ce1dcfbf374fd9d60df960bff046f92",
            "https://drscdn.500px.org/photo/127875875/h%3D600_k%3D1_a%3D1/9e667207de3ee01b72fec699a61a156f",
            "https://drscdn.500px.org/photo/127910615/h%3D600_k%3D1_a%3D1/9832834ff48dee33cca9a63c3680c391",
            "https://drscdn.500px.org/photo/127917691/h%3D600_k%3D1_a%3D1/569744eb7f6b0be651ef95b05409f283",
            "https://drscdn.500px.org/photo/127895003/h%3D600_k%3D1_a%3D1/aa9ba5e17219b6523e3576914281d014",
            "https://drscdn.500px.org/photo/127891201/h%3D600_k%3D1_a%3D1/11e7b89d61b3633d58e80bb4b91cfb96",
            "https://drscdn.500px.org/photo/127876087/h%3D600_k%3D1_a%3D1/beb9f8d4341e4c99aec0918081c29dfe",
            "https://drscdn.500px.org/photo/127866171/h%3D600_k%3D1_a%3D1/5100cdeb7006968a012ecf106c0fe28b",
            "https://drscdn.500px.org/photo/127868593/h%3D600_k%3D1_a%3D1/02ed979046028b417bb6e2214a8403e4",
            "https://drscdn.500px.org/photo/127868963/h%3D600_k%3D1_a%3D1/be27239695e8002979124bfdeb9730ad",
            "https://drscdn.500px.org/photo/127879079/h%3D600_k%3D1_a%3D1/d00277578f457e84eb36faa7740f4374"
    };*/
}
