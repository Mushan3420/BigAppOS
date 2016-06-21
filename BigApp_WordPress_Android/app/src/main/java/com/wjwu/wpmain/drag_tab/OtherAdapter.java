package com.wjwu.wpmain.drag_tab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wjwu.wpmain.lib_base.R;

import java.util.List;

import model.NavCatalog;

public class OtherAdapter extends BaseAdapter {
    private Context context;
    public List<NavCatalog> catalogList;
    private TextView item_text;
    boolean isVisible = true;
    public int remove_position = -1;

    public OtherAdapter(Context context, List<NavCatalog> channelList) {
        this.context = context;
        this.catalogList = channelList;
    }

    @Override
    public int getCount() {
        return catalogList == null ? 0 : catalogList.size();
    }

    @Override
    public NavCatalog getItem(int position) {
        if (catalogList != null && catalogList.size() > 0) {
            return catalogList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.drag_adapter_item, null);
        item_text = (TextView) view.findViewById(R.id.tv_item);
        NavCatalog channel = getItem(position);
        item_text.setText(channel.name);
        if (!isVisible && (position == -1 + catalogList.size())) {
            item_text.setText("");
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    public List<NavCatalog> getChannnelLst() {
        return catalogList;
    }

    public void addItem(NavCatalog channel) {
        catalogList.add(channel);
        notifyDataSetChanged();
    }

    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    public void remove() {
        catalogList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    public void setListDate(List<NavCatalog> list) {
        catalogList = list;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}