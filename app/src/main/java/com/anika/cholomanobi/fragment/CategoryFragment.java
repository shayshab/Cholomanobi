package com.anika.cholomanobi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anika.cholomanobi.R;
import com.anika.cholomanobi.adapter.CategoryAdapter;
import com.anika.cholomanobi.helper.ApiConfig;
import com.anika.cholomanobi.helper.Constant;
import com.anika.cholomanobi.helper.Session;
import com.anika.cholomanobi.helper.VolleyCallback;
import com.anika.cholomanobi.model.Category;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class CategoryFragment extends Fragment {

    public static ArrayList<Category> categoryArrayList;
    TextView txtnodata;
    RecyclerView categoryrecycleview;
    SwipeRefreshLayout swipeLayout;
    View root;
    Activity activity;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_category, container, false);

        activity = getActivity();

        setHasOptionsMenu(true);


        txtnodata = root.findViewById(R.id.txtnodata);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        progressBar = root.findViewById(R.id.progressBar);
        categoryrecycleview = root.findViewById(R.id.categoryrecycleview);
        categoryrecycleview.setLayoutManager(new GridLayoutManager(getContext(), Constant.GRIDCOLUMN));
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ApiConfig.isConnected(activity)) {
                    if (new Session(activity).isUserLoggedIn()) {
                        ApiConfig.getWalletBalance(activity, new Session(activity));
                    }
                    GetCategory();
                }
                swipeLayout.setRefreshing(false);
            }
        });

        if (ApiConfig.isConnected(activity)) {
            if (new Session(activity).isUserLoggedIn()) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
            }
            GetCategory();
        }

        return root;
    }

    void GetCategory() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //System.out.println("======cate " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        categoryArrayList = new ArrayList<>();
                        categoryArrayList.clear();
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson gson = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = gson.fromJson(jsonObject.toString(), Category.class);
                                categoryArrayList.add(category);
                            }
                            categoryrecycleview.setAdapter(new CategoryAdapter(getContext(), activity, categoryArrayList, R.layout.lyt_subcategory, "category", 0));
                            progressBar.setVisibility(View.GONE);
                        } else {
                            txtnodata.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            categoryrecycleview.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.CategoryUrl, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.title_category);
        getActivity().invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
    }
}