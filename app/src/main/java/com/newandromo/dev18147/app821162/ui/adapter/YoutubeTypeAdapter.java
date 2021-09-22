package com.newandromo.dev18147.app821162.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import androidx.paging.AsyncPagedListDiffer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;

public class YoutubeTypeAdapter extends RecyclerView.Adapter<YoutubeTypeAdapter.YoutubeTypeViewHolder> {
    private static final DiffUtil.ItemCallback<YoutubeVideoEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<YoutubeVideoEntity>() {
        @Override
        public boolean areItemsTheSame(YoutubeVideoEntity oldItem, YoutubeVideoEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(YoutubeVideoEntity oldItem, YoutubeVideoEntity newItem) {
            return oldItem.getId() == newItem.getId()
                    && oldItem.getTypeId() == newItem.getTypeId();
        }
    };

    private final AsyncPagedListDiffer<YoutubeVideoEntity> mDiffer =
            new AsyncPagedListDiffer<>(this, DIFF_CALLBACK);
    private Context mContext;
    private YoutubeTypeListener mListener;
    private boolean mIsChannel = true;

    public YoutubeTypeAdapter(Context context, @NotNull YoutubeTypeListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void submitList(PagedList<YoutubeVideoEntity> pagedList) {
        mDiffer.submitList(pagedList);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        YoutubeVideoEntity video = mDiffer.getItem(position);
        if (video != null) {
            return (long) video.getId();
        }
        return super.getItemId(position);
    }

    @NotNull
    @Override
    public YoutubeTypeViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_video, parent, false);
        return new YoutubeTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull YoutubeTypeViewHolder holder, int position) {
        YoutubeVideoEntity video = mDiffer.getItem(position);
        if (video != null) {
            holder.bindTo(video);
        } else {
            // Null defines a placeholder item - AsyncPagedListDiffer will automatically
            // invalidate this row when the actual object is loaded from the database
            holder.clear();
        }
    }

    public void setIsChannel(boolean isChannel) {
        mIsChannel = isChannel;
    }

    public interface YoutubeTypeListener {
        void onSelected(YoutubeVideoEntity video);

        void onContextMenuAction(YoutubeVideoEntity video);
    }

    public class YoutubeTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener {

        ImageView mThumbImageView;
        TextView mDurationTextView;
        TextView mTitleTextView;
        TextView mChannelTextView;
        TextView mDateTextView;
        RelativeLayout mViewsContainer;
        ImageView mIconViewImage;
        TextView mViewsTextView;

        YoutubeTypeViewHolder(View itemView) {
            super(itemView);
            mThumbImageView = itemView.findViewById(R.id.thumbnail);
            mDurationTextView = itemView.findViewById(R.id.duration);
            mTitleTextView = itemView.findViewById(R.id.title);
            mChannelTextView = itemView.findViewById(R.id.channel);
            mDateTextView = itemView.findViewById(R.id.date);
            mViewsContainer = itemView.findViewById(R.id.viewContainer);
            mIconViewImage = itemView.findViewById(R.id.views_icon);
            mViewsTextView = itemView.findViewById(R.id.views);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                YoutubeVideoEntity video = mDiffer.getItem(getAdapterPosition());
                if (video != null) mListener.onSelected(video);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (mListener != null) {
                YoutubeVideoEntity video = mDiffer.getItem(getAdapterPosition());
                if (video != null) mListener.onContextMenuAction(video);
            }
        }

        void bindTo(YoutubeVideoEntity video) {
            // itemView.setTag(entry.getId());
            mTitleTextView.setText(video.getTitle());

            if (!TextUtils.isEmpty(video.getChannel())) {
                mChannelTextView.setVisibility(mIsChannel ? View.GONE : View.VISIBLE);
                mChannelTextView.setText(video.getChannel());
            }

            String date = video.getDate();

            try {
                if (video.getDateMillis() != 0) {
                    CharSequence prettyTime = DateUtils.getRelativeTimeSpanString(
                            video.getDateMillis(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                    if (!TextUtils.isEmpty(prettyTime)) date = prettyTime.toString();
                } else {
                    String parsedDate = MyDateUtils.parseTimestampToString(date);
                    if (!TextUtils.isEmpty(parsedDate)) date = parsedDate;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mDateTextView.setText(date);

            mViewsTextView.setText(video.getViews());

            if (TextUtils.isEmpty(video.getViews())) {
                mViewsContainer.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(video.getDuration())) {
                mDurationTextView.setText(video.getDuration());
                mDurationTextView.setVisibility(View.VISIBLE);
            }

            try {
                int placeHolderColor = AppUtils.getPlaceholderColor(mContext);
                Picasso.get()
                        .load(video.getThumbUrl())
                        .placeholder(placeHolderColor)
                        .error(placeHolderColor)
                        .fit().centerCrop()
                        .into(mThumbImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void clear() {
            // itemView.invalidate();
            mDurationTextView.invalidate();
            mThumbImageView.invalidate();
            mTitleTextView.invalidate();
            mChannelTextView.invalidate();
            mDateTextView.invalidate();
            mViewsContainer.invalidate();
            mIconViewImage.invalidate();
            mViewsTextView.invalidate();
        }
    }
}
