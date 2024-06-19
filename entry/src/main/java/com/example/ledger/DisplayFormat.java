package com.example.ledger;

import ohos.agp.utils.Color;
import ohos.global.icu.text.SimpleDateFormat;
import ohos.global.icu.util.Calendar;

public class DisplayFormat {
   /**
    * 根据类型获取图标ID
      * @param cateItem 类型
      * @return 
    */
   public static int getIconID (String cateItem){
      String bigcate = cateItem.split(">")[0];// 获取大类
      int iconid = 0;
      switch (bigcate){
         case "食品酒水":
            iconid = ResourceTable.Media_ys;
            break;
         case "娱乐消费":
            iconid = ResourceTable.Media_game;
            break;
         case "学习进修":
            iconid = ResourceTable.Media_wenju;
            break;
         case "行车交通":
            iconid = ResourceTable.Media_ggjt;
            break;
         case "收入":
            iconid = ResourceTable.Media_sr;
      }
      return iconid;
   }

   /**
    * 根据类型获取颜色ID
    * @param kind 收入/支出
    * @return color 金额颜色
    */
   public static Color getMoneyColor(String kind){
      Color color;
      switch (kind){
         case "支":
            color = Color.GREEN;
            break;
         case "收":
            color = Color.RED;
            break;
         default:
            color = Color.RED;
            break;
      }
      return color;
   }

   /**
    * 根据统计状态获取显示信息
    * @param state
    * @param calender
    * @return
    * */
    public static String getTitleTimeStringByState(int state, Calendar calender){
      if (state == 0){// 按照日显示
         SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
         return sdf1.format(calender.getTime());
      }else if (state == 1){// 按照月显示
         SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
         return sdf2.format(calender.getTime());
      }else if (state == 2){// 按照年显示
         SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
         return sdf3.format(calender.getTime());
      }else if(state == 3){// 显示全部
         return "所有记录";
      }else{
         return "state number error";
      }
         
   }

   /**
    * 获取显示时间
    *
    * @param calendar
    * */
   public static String getShowCalenderText(Calendar calendar){
      Calendar todayCalender = Calendar.getInstance();

      String str ="";
      if(calendar.get(Calendar.YEAR) == todayCalender.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == todayCalender.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == todayCalender.get(Calendar.DAY_OF_MONTH)){
         int temp = calendar.get(Calendar.DAY_OF_MONTH) - todayCalender.get(Calendar.DAY_OF_MONTH);
         switch (temp){
            case 0:
               str = "今天";
               break;
            case 1:
               str = "明天";
               break;
            case 2:
               str = "后天";
               break;
            case -2:
               str = "前天";
               break;
            case -1:
               str = "昨天";
               break;
         }
      }

      if(calendar.get(Calendar.YEAR) == todayCalender.get(Calendar.YEAR)){
         SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm");
         str = str + sdf1.format(calendar.getTime());
      }else{
         SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
         str = str + sdf2.format(calendar.getTime());
      }

      return str;
      
   }


}