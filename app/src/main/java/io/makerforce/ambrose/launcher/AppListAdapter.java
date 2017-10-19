package io.makerforce.ambrose.launcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ambrosechua on 5/8/17.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private final List<AppItem> appList;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardLayout;
        public LinearLayout masterLayout;

        public ImageView icon;
        public TextView title;
        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            masterLayout = itemView.findViewById(R.id.masterLayout);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }

    public AppListAdapter(List<AppItem> appList, Context context) {
        this.appList = appList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app, parent, false);
        ViewHolder viewHolder = new ViewHolder(layoutView);
        return viewHolder;
    }

    private void startApp(int position) {
        String intentName = this.appList.get(position).getPackageName();
        Intent launchIntent = this.context.getPackageManager().getLaunchIntentForPackage(intentName);
        Log.d("AllApps", intentName);
        if (launchIntent != null) {
            Log.d("AllApps", "YAY: " + launchIntent.getPackage());
            this.context.startActivity(launchIntent);
        }
    }

    @Override
    public void onBindViewHolder(final AppListAdapter.ViewHolder holder, int position) {
        AppItem a = appList.get(holder.getAdapterPosition());

        holder.title.setText(a.getTitle());
        holder.description.setText(a.getDescription());
        if (a.getIcon() != null) {
            holder.icon.setImageDrawable(a.getIcon());
        }

        if (a.hasColor()) {
            holder.cardLayout.setCardBackgroundColor(a.getColor());
        } else if (a.getIcon() == null) {
            holder.cardLayout.setCardBackgroundColor(Color.parseColor("#ff0000"));
        } else {
            Bitmap b = drawableToBitmap(a.getIcon());
            Palette.from(b).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette p) {
                    Palette.Swatch s = p.getVibrantSwatch();
                    if (s == null) {
                        s = p.getDarkVibrantSwatch();
                    }
                    if (s == null) {
                        s = p.getMutedSwatch();
                    }
                    if (s == null) {
                        return;
                    }
                    holder.cardLayout.setCardBackgroundColor(s.getRgb());
                }
            });
        }

        if (a.getType() == AppItem.NORMAL_ICON) {
            holder.masterLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) holder.icon.getLayoutParams();
            l.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, context.getResources().getDisplayMetrics());
            l.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, context.getResources().getDisplayMetrics());
            holder.icon.setLayoutParams(l);
            holder.icon.setImageTintList(null);
        } else if (a.getType() == AppItem.SIMPLE_ICON) {
            holder.masterLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (a.getType() == AppItem.BANNER_IMAGE) {
            holder.masterLayout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) holder.icon.getLayoutParams();
            l.width = ViewGroup.LayoutParams.MATCH_PARENT;
            l.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            l.setMargins(0, 0, 0, 0);
            holder.icon.setLayoutParams(l);
            holder.icon.setImageTintList(null);
        }

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApp(holder.getAdapterPosition());
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return this.appList.size();
    }

}
