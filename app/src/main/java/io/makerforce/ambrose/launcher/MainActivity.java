package io.makerforce.ambrose.launcher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(3, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        int granted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (granted != PackageManager.PERMISSION_GRANTED) {
            Log.d("AllApps", "Requires permission");
        }

        mAdapter = new AppListAdapter(getAppListData(), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
    }

    private List<AppItem> getAppListData(){
        List<AppItem> listViewItems = readList();

        if (listViewItems == null) {
            listViewItems = new ArrayList<>();
        }

        return listViewItems;
    }

    private List<AppItem> readList() {
        File sdcard = Environment.getExternalStorageDirectory();
        PackageManager packageManager = getApplicationContext().getPackageManager();

        String json = null;
        try {
            InputStream is = new FileInputStream(new File(sdcard, "apps.json"));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        List<AppItem> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray apps = obj.getJSONArray("apps");

            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);

                String packageName = app.getString("package");
                String title = app.has("title") ? app.getString("title") : "Unknown";
                String description = app.has("description") ? app.getString("description") : "";
                String icon = app.has("icon") ? app.getString("icon") : "default.png";
                String color = app.has("color") ? app.getString("color") : "#ffffff";
                String type = app.has("type") ? app.getString("type") : "NORMAL_ICON";
                //boolean featured = app.has("featured") ? app.getBoolean("featured") : false;

                Drawable iconDrawable = null; // = getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
                Intent pi = packageManager.getLaunchIntentForPackage(packageName);
                if (pi != null) {
                    ResolveInfo ri = packageManager.resolveActivity(pi, PackageManager.MATCH_ALL);
                    iconDrawable = getFullResIcon(packageName, ri.activityInfo.getIconResource());
                }
                try {
                    iconDrawable = Drawable.createFromStream(new FileInputStream(new File(sdcard, icon)), icon);
                    Log.d("AllApps", icon);
                } catch (FileNotFoundException e) {
                    // do nothing
                }

                if (title == "Unknown" && pi != null) {
                    try {
                        title = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0));
                    } catch (PackageManager.NameNotFoundException e) {
                        // do nothing
                    }
                }


                int typeInt = AppItem.NORMAL_ICON;
                switch (type) {
                    case "NORMAL_ICON":
                        typeInt = AppItem.NORMAL_ICON;
                        break;
                    case "SIMPLE_ICON":
                        typeInt = AppItem.SIMPLE_ICON;
                        break;
                    case "BANNER_IMAGE":
                        typeInt = AppItem.BANNER_IMAGE;
                        break;
                }

                int colorInt = -1;
                try {
                    colorInt = Color.parseColor(color);
                } catch (IllegalArgumentException e) {
                    // do nothing
                }

                AppItem a = new AppItem(packageName, title, description, iconDrawable, typeInt, colorInt);
                list.add(a);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }

        return list;
    }

    private Drawable getFullResIcon(String packageName, int iconRes) {
        try {
            return getFullResIcon(getApplicationContext().getPackageManager().getResourcesForApplication(packageName), iconRes);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    private Drawable getFullResIcon(Resources resources, int iconRes) {
        try {
            return resources.getDrawableForDensity(iconRes, DisplayMetrics.DENSITY_XHIGH);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

}
