package com.newandromo.dev18147.app821162.views;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.newandromo.dev18147.app821162.R;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacingLeft;
    private int spacingRight;
    private int spacingTop;
    private int spacingBottom;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacingSide, int spacingTopBottom, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingLeft = spacingSide;
        this.spacingRight = spacingSide;
        this.spacingTop = spacingTopBottom;
        this.spacingBottom = spacingTopBottom;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (view.getId() == R.id.dummy_header_view) {
            super.getItemOffsets(outRect, view, parent, state);
        } else if (view.getId() == R.id.header_view_container
                || view.getId() == R.id.hidden_header_view) {
            outRect.bottom = spacingBottom;
        } else {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacingLeft - column * spacingLeft / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacingRight / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacingTop;
                }
                outRect.bottom = spacingBottom; // item bottom
            } else {
                outRect.left = column * spacingLeft / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacingRight - (column + 1) * spacingRight / spanCount; // spacing - (column + 1) * ((1f / spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacingTop; // item top
                }
            }
        }
    }
}
