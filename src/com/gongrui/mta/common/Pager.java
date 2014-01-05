package com.gongrui.mta.common;

/**
 * 
 * @author Administrator
 * index 用于显示页数 
 * View_Count 用于每一页上显示的个�?
 */
public class Pager {

	
	 private int index;  
     private int View_Count;  
     
     public Pager(int index, int View_Count) {  
         this.index = index;  
         this.View_Count = View_Count;  
     }  
   
     public int getIndex() {  
         return index;  
     }  
   
     public void setIndex(int index) {  
         this.index = index;  
     }  
   
     public int getView_Count() {  
         return View_Count;  
     }  
   
     public void setView_Count(int view_Count) {  
         View_Count = view_Count;  
     }  
}
