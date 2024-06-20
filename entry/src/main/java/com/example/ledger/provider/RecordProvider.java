package com.example.ledger.provider;

import com.example.ledger.DisplayFormat;
import com.example.ledger.ResourceTable;
import com.example.ledger.model.Record;
import ohos.agp.components.*;
import ohos.app.Context;

import java.util.List;

/**
 * 收支信息
 * */
public class RecordProvider extends BaseItemProvider {

    private List<Record> recordList; // 记录列表
    private Context context; // 当前界面

    ItemListener listener; // 点击事件监听


    public static interface ItemListener{
        public void click(int i,Record record);
    }

    public ItemListener getListener() {
        return listener;
    }
    public void setListener(ItemListener listener){
        this.listener=listener;
    }

    public RecordProvider(List<Record> recordList, Context context) {
        this.recordList = recordList;
        this.context = context;
    }
    public RecordProvider(){

    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordList.get(i);

    }

    @Override
    public long getItemId(int i) {
        return recordList.get(i).getId();
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        ComponentContainer container =(ComponentContainer) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_record_item_layout,null,false);
        Record record = recordList.get(i);
        
        // 获取组件
        DependentLayout layout =(DependentLayout) container.findComponentById(ResourceTable.Id_record_item_layout);

        Image iconImage = (Image) container.findComponentById(ResourceTable.Id_kind_image);
        Text cateitem = (Text) container.findComponentById(ResourceTable.Id_cate_item);
        //Text memo =(Text) container.findComponentById(ResourceTable.Id_memo);
        Text money =(Text) container.findComponentById(ResourceTable.Id_money);
        Text time =(Text) container.findComponentById(ResourceTable.Id_record_time);

        // 设置组件
        iconImage.setPixelMap(DisplayFormat.getIconID(record.getCategory()));// 通过类型获取并设置图标

        cateitem.setText(record.getCategory());// 设置类型
        //memo.setText(record.getMemo());// 设置备注
        money.setText(""+record.getAmount());// 设置金额
        money.setTextColor(DisplayFormat.getMoneyColor(record.getType()));// 设置金额颜色
        time.setText(record.getDate()+" "+record.getTime());// 设置时间

        layout.setClickedListener(new Component.ClickedListener() {// 创建点击时间
            @Override
            public void onClick(Component component) {
                listener.click(i,record); // 通过回调调用点击函数
            }
        });

        return container;
    }
}