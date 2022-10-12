package com.bjpowernode.crm.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * Excel文件工具类
 */
public class HSSFUtils {
    public static String getCellValue(HSSFCell cell){
        String res = "";
        if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
            res = cell.getStringCellValue();
        }else if(cell.getCellType()==HSSFCell.CELL_TYPE_BOOLEAN){
            res = cell.getBooleanCellValue()+"";
        }else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
            res = cell.getNumericCellValue()+"";
        }else if (cell.getCellType()==HSSFCell.CELL_TYPE_FORMULA){
            res = cell.getCellFormula();
        }else {
            res = "";
        }
        return res;
    }
}
