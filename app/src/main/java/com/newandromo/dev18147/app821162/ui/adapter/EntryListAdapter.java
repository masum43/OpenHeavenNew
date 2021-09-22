package com.newandromo.dev18147.app821162.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.AsyncPagedListDiffer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.newandromo.dev18147.app821162.SplashScreen;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_SHOW_DESCRIPTION;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_DOUBLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_LARGE;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryListViewHolder> {



    private int position1;
    private static final DiffUtil.ItemCallback<EntryEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<EntryEntity>()  {
        @Override
        public boolean areItemsTheSame(EntryEntity oldItem, @NotNull EntryEntity newItem) {
            // return oldItem.getId() == newItem.getId();
            if (!TextUtils.isEmpty(oldItem.getUrl()) && !TextUtils.isEmpty(newItem.getUrl()))
                return oldItem.getUrl().equals(newItem.getUrl());
            else return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NotNull EntryEntity oldItem, @NotNull EntryEntity newItem) {
            try {
                return oldItem.getId() == newItem.getId()
                        && oldItem.isUnread() == newItem.isUnread()
                        && oldItem.isBookmarked() == newItem.isBookmarked()
                        && oldItem.getDateMillis() == newItem.getDateMillis()
                        && oldItem.getUrl().equals(newItem.getUrl());
            } catch (Exception e) {
                e.printStackTrace();
                return oldItem.getId() == newItem.getId()
                        && oldItem.isUnread() == newItem.isUnread()
                        && oldItem.isBookmarked() == newItem.isBookmarked()
                        && oldItem.getDateMillis() == newItem.getDateMillis();
            }
        }
    };

    public interface EntryEventListener {
        void onSelected(int position);

        void onContextMenuCreated(View v, int position);
    }

    private final AsyncPagedListDiffer<EntryEntity> mDiffer =
            new AsyncPagedListDiffer<>(this, DIFF_CALLBACK);
    private final int colorPrimary, colorSecondary;
    private Context mContext;
    private int mLayout;
    private EntryEventListener mListener;



    public EntryListAdapter(Context context, int layout, @NotNull EntryEventListener listener) {
        this.mContext = context;
        this.mLayout = layout;
        this.mListener = listener;

        Resources.Theme theme = mContext.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.appTextColorPrimary, typedValue, true);
        colorPrimary = typedValue.data;
        theme.resolveAttribute(R.attr.appTextColorSecondary, typedValue, true);
        colorSecondary = typedValue.data;
        // setHasStableIds(true);
    }

    public void submitList(PagedList<EntryEntity> pagedList) {
        mDiffer.submitList(pagedList);
    }

    public EntryEntity getEntry(int position) {
        return mDiffer.getItem(position);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        EntryEntity entity = mDiffer.getItem(position);
        if (entity != null) {
            return (long) entity.getId();
        }
        return super.getItemId(position);
    }

    @NotNull
    @Override
    public EntryListViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new EntryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull EntryListViewHolder holder, int position) {
        //check what type of view our position is
        EntryEntity entity = mDiffer.getItem(position);
        if (entity != null) {
            holder.bindTo(entity);
        } else {
            // Null defines a placeholder item - AsyncPagedListDiffer will automatically
            // invalidate this row when the actual object is loaded from the database
            holder.clear();
        }
        this.position1 = position;



       if (position %6 ==0) {
           if(holder.fl_adplaceholder != null) {
               refreshAd(holder.fl_adplaceholder);
               holder.fl_adplaceholder.setVisibility(View.VISIBLE);
           }
        }else {
           if(holder.fl_adplaceholder != null) {
               holder.fl_adplaceholder.setVisibility(View.GONE);
           }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSelected(position);
                }
            }
        });
        holder. itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
                mListener.onContextMenuCreated(v, position);
            }
        });


    }

   /* @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onSelected(position1);
        }
    }*/

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        mListener.onContextMenuCreated(v, position1);
    }*/

    public class EntryListViewHolder extends RecyclerView.ViewHolder  {

        ImageView mImageView;
        View mBookmarkView;
        TextView mTitleTextView;
        TextView mDescriptionTextView;
        TextView mDateTextView;

        private FrameLayout fl_adplaceholder;

        EntryListViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mBookmarkView = itemView.findViewById(R.id.bookmark);
            mTitleTextView = itemView.findViewById(R.id.titleTextView);
            mDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            mDateTextView = itemView.findViewById(R.id.dateTextView);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
        }



        void bindTo(EntryEntity entry) {
            // itemView.setTag(entry.getId());

            try {
                mTitleTextView.setText(Jsoup.parse(entry.getTitle(), "", Parser.htmlParser()).text());
            } catch (Exception e) {
                mTitleTextView.setText(entry.getTitle());
                e.printStackTrace();
            }

            String date = entry.getDate();

            try {
                if (entry.getDateMillis() != 0) {
                    CharSequence prettyTime = DateUtils.getRelativeTimeSpanString(
                            entry.getDateMillis(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                    if (!TextUtils.isEmpty(prettyTime)) date = prettyTime.toString();
                } else {
                    String parsedDate = MyDateUtils.parseTimestampToString(date);
                    if (!TextUtils.isEmpty(parsedDate)) date = parsedDate;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mDateTextView.setText(date);

            String excerpt = entry.getExcerpt();

            String layoutType = PrefUtils.getLayoutType(mContext);

            if (IS_SHOW_DESCRIPTION
                    && !LAYOUT_TYPE_DOUBLE.equals(layoutType)
                    && !LAYOUT_TYPE_LARGE.equals(layoutType)) {
                try {
                    excerpt = Jsoup.parse(excerpt, "", Parser.htmlParser()).text();
                    if (AppUtils.hasNougat()) {
                        excerpt = String.valueOf(Html.fromHtml(excerpt, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        excerpt = String.valueOf(Html.fromHtml(excerpt));
                    }
                    if (!TextUtils.isEmpty(excerpt)) {
                        mDescriptionTextView.setVisibility(View.VISIBLE);
                        mDescriptionTextView.setText(excerpt);
                    } else {
                        mDescriptionTextView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    mDescriptionTextView.setText(excerpt);
                    if (!TextUtils.isEmpty(mDescriptionTextView.getText()))
                        mDescriptionTextView.setVisibility(View.VISIBLE);
                }
            }

            if (entry.isUnread() == 1) {
                mTitleTextView.setTextColor(colorPrimary);
                mDescriptionTextView.setTextColor(colorSecondary);
            } else {
                mTitleTextView.setTextColor(colorSecondary);
                mDescriptionTextView.setTextColor(colorSecondary);
            }

            String thumbUrl = entry.getThumbUrl();

            if (!TextUtils.isEmpty(thumbUrl)) {
                mImageView.setVisibility(View.VISIBLE);

                try {
                    int placeHolderColor = AppUtils.getPlaceholderColor(mContext);
                    if (LAYOUT_TYPE_LARGE.equals(layoutType)) {
                        Picasso.get()
                                .load(thumbUrl)
                                .placeholder(placeHolderColor)
                                .fit().centerCrop()
                                //.error(placeHolderColor)
                                .into(mImageView);
                    } else {
                        Picasso.get()
                                .load(thumbUrl)
                                .placeholder(placeHolderColor)
                                .error(placeHolderColor)
                                .fit().centerCrop()
                                .into(mImageView);
                    }
                } catch (Exception e) {
                    mImageView.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            } else {
                mImageView.setVisibility(View.GONE);
            }

            mBookmarkView.setVisibility(entry.isBookmarked() == 1 ? View.VISIBLE : View.GONE);
        }

        void clear() {
            // itemView.invalidate();
            mImageView.invalidate();
            mBookmarkView.invalidate();
            mTitleTextView.invalidate();
            mDescriptionTextView.invalidate();
            mDateTextView.invalidate();
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
//        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
     /*   VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.

                    super.onVideoEnd();
                }
            });
        } else {

        }*/
    }

    private NativeAd nativeAds =   null;
    private void refreshAd(FrameLayout frameLayout) {
        SplashScreen.showApplobinMrecAd((Activity) mContext, frameLayout);

//        AdLoader.Builder builder = new AdLoader.Builder(mContext, mContext.getString(R.string.admob_native));
//
//        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//            @Override
//            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
//
//                if (nativeAds != null) {
//                    nativeAds.destroy();
//                }
//                nativeAds = nativeAd;
//             /*   FrameLayout frameLayout =
//                        findViewById(R.id.fl_adplaceholder);*/
//                try {
//                    LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//                    NativeAdView adView = (NativeAdView)inflater
//                            .inflate(R.layout.ad_unified, null);
//                    populateUnifiedNativeAdView(nativeAd, adView);
//                    frameLayout.removeAllViews();
//                    frameLayout.addView(adView);
//
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        VideoOptions videoOptions = new VideoOptions.Builder()
//                .setStartMuted(true)
//                .build();
//
//        NativeAdOptions adOptions = new NativeAdOptions.Builder()
//                .setVideoOptions(videoOptions)
//                .build();
//
//        builder.withNativeAdOptions(adOptions);
//
//        AdLoader adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
////                super.onAdFailedToLoad(loadAdError);
//             Log.e("abc", "=========loadAdError======" + loadAdError);
//            }
//        }).build();
//
//
//        adLoader.loadAd(new AdRequest.Builder().build());


    }
}
