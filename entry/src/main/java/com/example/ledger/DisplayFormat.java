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
      int iconid = 0;
      switch (cateItem){
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
         case "生活费":
            iconid = ResourceTable.Media_shf;
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
      if (kind.equals("income")) {
         color = Color.RED;
      } else {
         color = Color.GREEN;
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

}