package com.example.ledger.slice;

import com.example.ledger.DisplayFormat;
import com.example.ledger.ResourceTable;
import com.example.ledger.model.Record;
import com.example.ledger.provider.PageProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.global.icu.text.SimpleDateFormat;
import ohos.global.icu.util.Calendar;
import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.Date;


public class AddBookSlice extends AbilitySlice {

    Uri uri = Uri.parse("dataability:///com.example.ledger.DataAbility/record");
    private DataAbilityHelper dataAbilityHelper;
    private DataAbilityPredicates predicates;
    String[] columns = {"id","year","month","day","time","amount","type","category"};


    private Text input_text;
    private Text output_text;
    private Color ChooseColor,unChooseColor;
    private double money;
    private String cateItem= "收入";
    private Calendar datetime = Calendar.getInstance();
    private Button save_btn1;
    private Button add_record_btn;
    private Image back_btn;

    DisplayFormat displayFormat = new DisplayFormat();
    private String type="income";




    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_add_book_layout);

        // 初始化页面
        initComponent();

        // 添加按钮响应事件
        addListener();
    }

    /**
     * 按钮响应事件
     */
    private void addListener() {
        save_btn1.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                int hour = datetime.get(Calendar.HOUR_OF_DAY);
                int minute = datetime.get(Calendar.MINUTE);
                int second = datetime.get(Calendar.SECOND);
                String date = new SimpleDateFormat("yyyy-MM-dd").format(datetime);
                String time = hour+ ":" + minute + ":" + second;
                Record record = new Record(date,time,money,type,cateItem);

                insertRecord(record);
            }
        });

        back_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                AbilitySlice slice = new BookSlice();
                Intent intent =new Intent();
                present(slice,intent);
            }
        });
    }
    private void insertRecord(Record record){
        dataAbilityHelper = DataAbilityHelper.creator(this);
        ValuesBucket values = new ValuesBucket();
        values.putString("year", record.getDate().split("-")[0]);
        values.putString("month", record.getDate().split("-")[1]);
        values.putString("day", record.getDate().split("-")[2]);
        values.putString("time", record.getTime());
        values.putDouble("amount", record.getAmount());
        values.putString("type", record.getType());
        values.putString("category", record.getCategory());
        try{
            int result = dataAbilityHelper.insert(uri, values);
            new ToastDialog(this).setText("插入成功！" ).show();
        }catch(DataAbilityRemoteException e){
            new ToastDialog(this).setText("插入失败！" ).show();
            e.printStackTrace();
        }
    }

    /**
     * 初始化页面（初始化页面组件）
     * done
     */
    private void initComponent() {
        input_text = (Text) findComponentById(ResourceTable.Id_input_text);
        output_text = (Text) findComponentById(ResourceTable.Id_output_text);
        ChooseColor = input_text.getTextColor();
        unChooseColor = output_text.getTextColor();

        save_btn1 = (Button) findComponentById(ResourceTable.Id_save1_btn);
        back_btn = (Image) findComponentById(ResourceTable.Id_back_btn);

        initPageSlider();
    }


    private void initPageSlider() {
        //绑定PageProvider
        PageSlider pageSlider = (PageSlider) findComponentById(ResourceTable.Id_page_slider);
        pageSlider.setProvider(new PageProvider(getData(),this));// 传入数据
        pageSlider.setCurrentPage(0);// 设置当前页
        pageSlider.setSlidingPossible(true);// 设置可以滑动
        pageSlider.setReboundEffect(true);// 设置回弹效果

        pageSlider.addPageChangedListener(new PageSlider.PageChangedListener() {
            @Override
            public void onPageSliding(int i, float v, int i1) {

            }

            @Override
            public void onPageSlideStateChanged(int i) {

            }

            @Override
            public void onPageChosen(int i) {
                if(i == 0){// 收入页面
                    type = "income";
                    input_text.setTextColor(ChooseColor);
                    output_text.setTextColor(unChooseColor);

                    cateItem = "收入";
                    money = 0.00;
                    datetime = Calendar.getInstance();
                }else if(i == 1){
                    type="expense";
                    input_text.setTextColor(unChooseColor);
                    output_text.setTextColor(ChooseColor);

                    cateItem="食品酒水";
                    money = 0.00;
                    datetime = Calendar.getInstance();
                }
            }
        });
        //pageSlider.setProvider(new PageSlider());
    }

    /**
     * 获取页面PageProvider.
     */
    private ArrayList<PageProvider.DataItem> getData() {
        ArrayList<PageProvider.DataItem> dataItems = new ArrayList<>();
        dataItems.add(new PageProvider.DataItem("input",ResourceTable.Layout_add_input_book_layout));
        dataItems.add(new PageProvider.DataItem("output",ResourceTable.Layout_add_output_book_layout));
        return dataItems;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getCateItem() {
        return cateItem;
    }

    public void setCateItem(String cateItem) {
        this.cateItem = cateItem;
    }
    public void setDatetime(Calendar datetime) {
        this.datetime = datetime;
    }

}

