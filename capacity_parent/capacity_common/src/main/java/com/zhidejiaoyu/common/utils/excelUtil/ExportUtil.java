package com.zhidejiaoyu.common.utils.excelUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 文件导出工具类
 *
 * @author wuchenxi
 * @date 2018/6/14 9:48
 */
public class ExportUtil {

    /**
     * 发送响应流方法(导出excel)
     *
     * @param response
     * @param fileName
     */
    public static void exportExcel(HttpServletResponse response, String fileName, HSSFWorkbook wb) throws Exception {
        fileName = new String(fileName.getBytes(), "ISO-8859-1");
        response.setContentType("application/octet-stream;charset=ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");

        OutputStream os = response.getOutputStream();
        wb.write(os);
        os.flush();
        os.close();
    }
}
