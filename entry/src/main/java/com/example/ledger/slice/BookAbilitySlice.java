package com.example.ledger.slice;

import com.example.ledger.DisplayFormat;
import com.example.ledger.ResourceTable;
import com.example.ledger.model.Const;
import com.example.ledger.model.RecordBean;
import com.example.ledger.model.RecordDbStore;
import com.example.ledger.provider.RecordProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.global.icu.text.SimpleDateFormat;
import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;


import java.util.List;
import java.util.Random;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;


public class BookAbilitySlice extends AbilitySlice {
    private Text curDateText;
    private Text curWeekText;
    private Text dayInputText;
    private Text dayOutputText;
    private Text balanceText;
    private Image show_more_btn;
    private Image add_record_btn;
    private ListContainer curDayRecordContainer;// 当天记录
    private final String[] weeks={"","周日","周一","周二","周三","周四","周五","周六"};

    private Calendar calendar; // 用于记录程序当前的时间
    
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_book_layout);

        addTextData();// 向数据库随机添加一些记录

        calendar = Calendar.getInstance();// 获取系统当前时间

        // 初始化页面
        initComponent();

        // 添加响应事件
        //addListener();

    }

    /**
     * 初始化页面（初始化页面组件）
     */
    private void initComponent() {
        // 获取页面组件
        curDateText = (Text) findComponentById(ResourceTable.Id_current_date_id);
        curWeekText = (Text) findComponentById(ResourceTable.Id_current_week);
        dayInputText = (Text) findComponentById(ResourceTable.Id_current_day_input_money);
        dayOutputText = (Text) findComponentById(ResourceTable.Id_current_day_output_money);
        balanceText = (Text) findComponentById(ResourceTable.Id_balance_text);
        curDayRecordContainer = (ListContainer) findComponentById(ResourceTable.Id_current_day_record_ListContainer) ;
        show_more_btn = (Image) findComponentById(ResourceTable.Id_show_more_btn);
        add_record_btn = (Image) findComponentById(ResourceTable.Id_add_record_btn);

        // 设置当前日期
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        curDateText.setText("今天"+sdf1.format(calendar.getTime()));

        // 设置当前星期
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        curWeekText.setText(weeks[calendar.get(Calendar.DAY_OF_WEEK)]);

        // 更新记录
        reloadRecord();
    }



    /**
     * 更新记录
     * 
     * */
    private void reloadRecord(){
        double[] money = getInputOutputMoney(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_WEEK)); //传入当前日期，获取收支情况
        dayOutputText.setText(String.format("%.2f",money[0]));// 设置当天输出
        dayInputText.setText(String.format("%.2f",money[1]));

        // 设置结余信息
        if(money[2]>0){
            balanceText.setText("+"+String.format("%.2f",money[2]));
        }else{
            balanceText.setText(String.format("%.2f",money[2]));
        }

        // 查询当天的记录列表
        RecordProvider provider = new RecordProvider(getRecordListByDay(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)),this);

        // 设置列表的点击事件
        provider.setListener(new RecordProvider.ItemListener() {
            @Override
            public void click(int i, RecordBean bean) {
                // 显示明细
                ShowRecordDetail(bean);
            }
        });

        // 设置数据提供
        curDayRecordContainer.setItemProvider(provider);

    }
    /**
     * 获取当天的支出以及收入的金额
     * @return money[0] 为当天的支出金额，money[1] 为当天的收入金额，money[2] 为当天的结余金额
     */
    private double[]  getInputOutputMoney(int year,int month,int day){
        double[] money = {0.0,0.0,0.0};
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS,Const.DB_NAME, RecordDbStore.class);
        OrmPredicates ormPredicates =  ormContext.where(RecordBean.class).equalTo("year",year).equalTo("month",month).equalTo("day",day);// 查询当天的收入和支出
        List<RecordBean> recordList= ormContext.query(ormPredicates);
        for (RecordBean recordBean:recordList){
            if(recordBean.getKind().equals("支")){
                money[0] += recordBean.getMoney();
            }else{
                money[1] += recordBean.getMoney();
            }
        }

        money[2] = money[1] - money[0];
        ormContext.flush();
        ormContext.close();

        return money;
    }

    /**
     * 向数据库中添加一些测试数据
     */
    private void addTextData(){
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS,Const.DB_NAME, RecordDbStore.class);

        String[] kindlist ={"收","支"};
        String[][] catelist ={{"收入>生活费","收入>家庭收入","收入>奖学金"},{"食品酒水>饮料","学习进修>学习工具","行车交通>公共交通","娱乐消费>游戏消费"}};// 收入支出类型

        Random random = new Random();// 随机函数生成金额
        Calendar testCalender = Calendar.getInstance();

        for(int year = 2021; year<=2024;year++){// 随机创建一些记录
            for(int month = 1;month<=12;month++){
                for(int day =1;day<=29; day++){
                    testCalender.set(year,month,day);
                    for(int i =0;i<12;i++){
                        int index1 =random.nextInt(2);
                        int index2 = random.nextInt(catelist[index1].length);

                        try{
                            Thread.sleep(1);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        RecordBean recordBean = new RecordBean(System.currentTimeMillis(),kindlist[index1],
                                catelist[index1][index2],(double) random.nextInt(2000),"memo",testCalender);

                        ormContext.insert(recordBean);
                    }
                }
            }
        }
        ormContext.flush();
        ormContext.close();

    }


    /**
     * 展示详细信息
     * */
    private void ShowRecordDetail(RecordBean bean) {
        CommonDialog cd = new CommonDialog(getContext());
        cd.setCornerRadius(50);

        // 绑定layout文件
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_record_detail_layout,null,false);

        Image image = (Image) dl.findComponentById(ResourceTable.Id_icon_image);
        image.setPixelMap(DisplayFormat.getIconID(bean.getCateItem()));// 设置图片为类型图片

        Text money_text = (Text) dl.findComponentById(ResourceTable.Id_money_text);
        String flag = "+";
        if(bean.getKind().equals("支")){
            flag = "-";
        }
        money_text.setText(flag+String.format("%.2f",bean.getMoney()));
        money_text.setTextColor(DisplayFormat.getMoneyColor(bean.getKind()));

        Text cate_text = (Text) dl.findComponentById(ResourceTable.Id_cate_text);
        cate_text.setText(bean.getCateItem());

        Text time_text = (Text) dl.findComponentById(ResourceTable.Id_time_text);
        //time_text.setText(DisplayFormat.getShowCalenderText(bean.getTime()));

        Text memo_text = (Text) dl.findComponentById(ResourceTable.Id_memo_text);
        memo_text.setText(bean.getMemo());

        Button btn_ok = (Button) dl.findComponentById(ResourceTable.Id_btn_ok);
        btn_ok.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                cd.destroy();
            }
        });
        
        Button btn_delete = (Button) dl.findComponentById(ResourceTable.Id_btn_delete);
        btn_delete.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                deleteRecordByID(bean.getId());

                reloadRecord();

                cd.destroy();
            }
        });

        cd.setSize(MATCH_PARENT,MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.setAlignment(LayoutAlignment.BOTTOM);
        cd.show();

    }


    /**
     * 通过id删除某条记录
     */
    private void deleteRecordByID(long id) {
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS,Const.DB_NAME, RecordDbStore.class);


        OrmPredicates ormPredicates =  ormContext.where(RecordBean.class).equalTo("id",id);
        List<RecordBean> recordList= ormContext.query(ormPredicates);
        
        if(recordList.size() == 0){// 查询不到记录就直接返回
            ormContext.flush();
            ormContext.close();

            return;
        }

        RecordBean bean = recordList.get(0);
        if(ormContext.delete(bean)){
            // HiLog.info(Const.LOG_LABEL,"delete success");
        }else{
            // HiLog.info(Const.LOG_LABEL,"delete failed");
        }
        ormContext.flush();
        ormContext.close();
        
    }

    /**
     * 查询数据库中某一天的记录
     *
     * */
    private List<RecordBean> getRecordListByDay(int year, int month, int day) {
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS,Const.DB_NAME, RecordDbStore.class);

        OrmPredicates ormPredicates =  ormContext.where(RecordBean.class).equalTo("year",year).equalTo("month",month).equalTo("day",day);// 查询当天的收入和支出
        List<RecordBean> recordList= ormContext.query(ormPredicates);

        ormContext.flush();
        ormContext.close();
        return recordList;
    }

}
