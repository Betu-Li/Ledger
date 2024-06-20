package com.example.ledger.provider;

import com.example.ledger.DisplayFormat;
import com.example.ledger.ResourceTable;
import com.example.ledger.slice.AddBookSlice;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.global.icu.text.SimpleDateFormat;
import ohos.global.icu.util.Calendar;
import ohos.global.resource.Resource;

import java.util.List;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class PageProvider extends PageSliderProvider {

    private List<DataItem> list;
    private AddBookSlice slice;
    private Calendar todayCalender =Calendar.getInstance();
    DisplayFormat displayFormat = new DisplayFormat();

    //类别
    int stringTitleIndex = 0;
    String[] cateTitle_out = {"食品酒水","娱乐消费","学习进修","行车交通"};
    String[] cateTitle_in = {"收入","生活费"};
    int Index = 0;

    public PageProvider(List<DataItem> list, AddBookSlice slice) {
        this.list = list;
        this.slice = slice;

    }

    public PageProvider() {

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {

        final DataItem data = list.get(i);
        Component cpt = LayoutScatter.getInstance(slice).parse(data.mLayout, null,true);

        //输入金额
        TextField moneyTextField = (TextField) cpt.findComponentById(ResourceTable.Id_money);
        moneyTextField.addTextObserver(new Text.TextObserver() {
            @Override
            public void onTextUpdated(String s, int i, int i1, int i2) {
                slice.setMoney(Double.parseDouble(s));
            }
        });

        //分类实现(收入、支出)
        if(i == 1){
            //获取分类
            Text cateItemtext=(Text) cpt.findComponentById(ResourceTable.Id_cate_text);
            cateItemtext.setText(cateTitle_out[0]);

            DirectionalLayout itemlayout = (DirectionalLayout) cpt.findComponentById(ResourceTable.Id_cateitem_layout);
            itemlayout.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    CommonDialog cd = new CommonDialog(slice.getContext());
                    cd.setCornerRadius(50);

                    DirectionalLayout dl =(DirectionalLayout) LayoutScatter.getInstance(slice.getContext()).parse(ResourceTable.Layout_cateitem_layout,null,false);

                    //设置picker
                    Picker catePicker =(Picker) dl.findComponentById(ResourceTable.Id_cateitem_picker);
                    catePicker.setWheelModeEnabled(true);//可滑动
                    catePicker.setMaxValue(cateTitle_out.length-1);
                    catePicker.setMinValue(0);
                    catePicker.setFormatter(new Picker.Formatter() {// 设置展示模式
                        @Override
                        public String format(int i) {
                            return cateTitle_out[i];
                        }
                    });

                    catePicker.setValue(0);// 设置默认值

                    //设置监听事件
                    catePicker.setValueChangedListener(new Picker.ValueChangedListener() {
                        @Override
                        public void onValueChanged(Picker picker, int i, int i1) {
                            Index = i1;
                        }
                    });
                    Button btn_cancel =(Button) dl.findComponentById(ResourceTable.Id_cateitem_cancel);
                    btn_cancel.setClickedListener(new Component.ClickedListener() {
                        @Override
                        public void onClick(Component component) {
                            cd.destroy();
                        }
                    });

                    Button btn_ok = (Button)  dl.findComponentById(ResourceTable.Id_cateitem_ok);
                    btn_ok.setClickedListener(new Component.ClickedListener() {
                        @Override
                        public void onClick(Component component) {
                            cateItemtext.setText(cateTitle_out[Index]);
                            slice.setCateItem(cateTitle_out[Index]);
                            cd.destroy();
                        }
                    });
                    cd.setSize(MATCH_PARENT,MATCH_CONTENT);
                    cd.setContentCustomComponent(dl);
                    cd.setAlignment(LayoutAlignment.BOTTOM);
                    cd.show();
                }
            });


        }
        else if (i == 0){
            //获取分类
            Text cateItemtext=(Text) cpt.findComponentById(ResourceTable.Id_cate_text);
            cateItemtext.setText(cateTitle_in[0]);

            DirectionalLayout itemlayout = (DirectionalLayout) cpt.findComponentById(ResourceTable.Id_cateitem_layout);
            itemlayout.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    CommonDialog cd = new CommonDialog(slice.getContext());
                    cd.setCornerRadius(50);

                    DirectionalLayout dl =(DirectionalLayout) LayoutScatter.getInstance(slice.getContext()).parse(ResourceTable.Layout_cateitem_layout,null,false);

                    //设置picker
                    Picker catePicker =(Picker) dl.findComponentById(ResourceTable.Id_cateitem_picker);
                    catePicker.setWheelModeEnabled(true);//可滑动
                    catePicker.setMaxValue(cateTitle_in.length-1);
                    catePicker.setMinValue(0);
                    catePicker.setFormatter(new Picker.Formatter() {// 设置展示模式
                        @Override
                        public String format(int i) {
                            return cateTitle_in[i];
                        }
                    });

                    catePicker.setValue(0);// 设置默认值

                    //设置监听事件
                    catePicker.setValueChangedListener(new Picker.ValueChangedListener() {
                        @Override
                        public void onValueChanged(Picker picker, int i, int i1) {
                            Index = i1;
                        }
                    });
                    Button btn_cancel =(Button) dl.findComponentById(ResourceTable.Id_cateitem_cancel);
                    btn_cancel.setClickedListener(new Component.ClickedListener() {
                        @Override
                        public void onClick(Component component) {
                            cd.destroy();
                        }
                    });

                    Button btn_ok = (Button)  dl.findComponentById(ResourceTable.Id_cateitem_ok);
                    btn_ok.setClickedListener(new Component.ClickedListener() {
                        @Override
                        public void onClick(Component component) {
                            cateItemtext.setText(cateTitle_in[Index]);
                            slice.setCateItem(cateTitle_in[Index]);
                            cd.destroy();
                        }
                    });
                    cd.setSize(MATCH_PARENT,MATCH_CONTENT);
                    cd.setContentCustomComponent(dl);
                    cd.setAlignment(LayoutAlignment.BOTTOM);
                    cd.show();
                }
            });


        }

        //时间设置
        Text calenderText = (Text) cpt.findComponentById(ResourceTable.Id_time_text);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calenderText.setText(sdf.format(todayCalender));
        calenderText.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                CommonDialog cd =new CommonDialog(slice.getContext());
                cd.setCornerRadius(50);

                DirectionalLayout dl =(DirectionalLayout) LayoutScatter.getInstance(slice.getContext()).parse(ResourceTable.Layout_calender_layout,null,false);

                //设置picker
                DatePicker datePicker =(DatePicker) dl.findComponentById(ResourceTable.Id_date_picker);
                datePicker.setWheelModeEnabled(true);//可滑动

                TimePicker timePicker = (TimePicker) dl.findComponentById(ResourceTable.Id_time_picker);
                timePicker.setWheelModeEnabled(true);//可滑动
                timePicker.showSecond(true);

                Button btn_cancel =(Button) dl.findComponentById(ResourceTable.Id_calender_cancel);
                btn_cancel.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        cd.destroy();
                    }
                });

                Button btn_ok = (Button) dl.findComponentById(ResourceTable.Id_calender_ok);
                btn_ok.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        int year = datePicker.getYear();
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth()-1;
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        int second = timePicker.getSecond();
                        todayCalender.set(year,month,day,hour,minute,second);
                        calenderText.setText(sdf.format(todayCalender));
                        slice.setDatetime(todayCalender);
                        
                        cd.destroy();
                    }
                });
                cd.setSize(MATCH_PARENT,MATCH_CONTENT);
                cd.setContentCustomComponent(dl);
                cd.setAlignment(LayoutAlignment.BOTTOM);
                cd.show();
            }
        });

        componentContainer.addComponent(cpt);
        
        return cpt;

    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        componentContainer.removeComponent((Component) o);
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return false;
    }

    // 数据实体类
    public static class DataItem{
        String mText;
        int mLayout;

        public DataItem(String mText, int mLayout) {
            this.mText = mText;
            this.mLayout = mLayout;
        }

        public DataItem() {

        }
    }



}