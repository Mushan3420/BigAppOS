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

public class DragAdapter extends BaseAdapter {
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 列表数据是否改变
     */
    private boolean isListChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    public List<NavCatalog> catalogList;
    /**
     * TextView 分类内容
     */
    private TextView item_text;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public DragAdapter(Context context, List<NavCatalog> channelList) {
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
        if (position == 0) {
            item_text.setEnabled(false);
            view.setEnabled(false);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            view.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + catalogList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            view.setEnabled(true);
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    /**
     * 添加类别列表
     */
    public void addItem(NavCatalog channel) {
        catalogList.add(channel);
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 拖动变更类别排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        NavCatalog dragItem = getItem(dragPostion);
        if (dragPostion < dropPostion) {
            catalogList.add(dropPostion + 1, dragItem);
            catalogList.remove(dragPostion);
        } else {
            catalogList.add(dropPostion, dragItem);
            catalogList.remove(dragPostion + 1);
        }
        isChanged = true;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取类别列表
     */
    public List<NavCatalog> getChannnelLst() {
        return catalogList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除类别列表
     */
    public void remove() {
        catalogList.remove(remove_position);
        remove_position = -1;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 设置类别列表
     */
    public void setListDate(List<NavCatalog> list) {
        catalogList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 排序是否发生改变
     */
    public boolean isListChanged() {
        return isListChanged;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}