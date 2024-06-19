package com.example.ledger.provider;

import com.example.ledger.DisplayFormat;
import com.example.ledger.model.RecordBean;
import ohos.agp.components.*;
import ohos.app.Context;

import com.example.ledger.ResourceTable;

import java.util.List;

/**
 * 收支信息
 * */
public class RecordProvider extends BaseItemProvider {

    private List<RecordBean> recordBeanList; // 记录列表
    private Context context; // 当前界面

    ItemListener listener; // 点击事件监听


    public static interface ItemListener{
        public void click(int i,RecordBean bean);
    }

    public ItemListener getListener() {
        return listener;
    }
    public void setListener(ItemListener listener){
        this.listener=listener;
    }

    public RecordProvider(List<RecordBean> recordBeanList, Context context) {
        this.recordBeanList = recordBeanList;
        this.context = context;
    }
    public RecordProvider(){

    }

    @Override
    public int getCount() {
        return recordBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordBeanList.get(i);

    }

    @Override
    public long getItemId(int i) {
        return recordBeanList.get(i).getId();
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        ComponentContainer container =(ComponentContainer) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_record_item_layout,null,false);
        RecordBean bean = recordBeanList.get(i);
        
        // 获取组件
        DependentLayout layout =(DependentLayout) container.findComponentById(ResourceTable.Id_record_item_layout);

        Image iconImage = (Image) container.findComponentById(ResourceTable.Id_kind_image);
        Text cateitem = (Text) container.findComponentById(ResourceTable.Id_cate_item);
        Text memo =(Text) container.findComponentById(ResourceTable.Id_memo);
        Text money =(Text) container.findComponentById(ResourceTable.Id_money);
        Text time =(Text) container.findComponentById(ResourceTable.Id_record_time);

        // 设置组件
        iconImage.setPixelMap(DisplayFormat.getIconID(bean.getCateItem()));// 通过类型获取并设置图标

        cateitem.setText(bean.getCateItem().split(">")[1]);// 设置类型
        memo.setText(bean.getMemo());// 设置备注
        money.setText(""+bean.getMoney());// 设置金额
        money.setTextColor(DisplayFormat.getMoneyColor(bean.getKind()));// 设置金额颜色
        time.setText(bean.getRecordSimpleTime());// 设置时间

        layout.setClickedListener(new Component.ClickedListener() {// 创建点击时间
            @Override
            public void onClick(Component component) {
                listener.click(i,bean); // 通过回调调用点击函数
            }
        });

        return container;
    }
}