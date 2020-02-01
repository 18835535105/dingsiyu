package com.zhidejiaoyu.common.utils.excelUtil.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * ExcelUtil
 * 基于easyExcel的开源框架，poi版本3.17
 * BeanCopy ExcelException 属于自定义数据，属于可自定义依赖
 * 工具类尽可能还是需要减少对其他java的包的依赖
 *
 * @author wenxuan.wang
 */
public class ExcelUtil {
    /**
     * 私有化构造方法
     */
    private ExcelUtil() {
    }

    /**
     * 读取 Excel(多个 sheet)
     * 将多sheet合并成一个list数据集，通过自定义ExcelReader继承AnalysisEventListener
     * 重写invoke doAfterAllAnalysed方法
     * getExtendsBeanList 主要是做Bean的属性拷贝 ，可以通过ExcelReader中添加的数据集直接获取
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> List<T> readExcel(MultipartFile excel, Class<T> rowModel) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);
        for (Sheet sheet : reader.getSheets()) {
            sheet.setClazz(rowModel);
            reader.read(sheet);
        }
        return getExtendsBeanList(excelListener.getDataList(), rowModel);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @param sheetNo  sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> List<T> readExcel(MultipartFile excel, Class<T> rowModel, int sheetNo) {
        return readExcel(excel, rowModel, sheetNo, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel       文件
     * @param rowModel    实体类映射，继承 BaseRowModel 类
     * @param sheetNo     sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> List<T> readExcel(MultipartFile excel, Class<T> rowModel, int sheetNo,
                                                             int headLineNum) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);
        reader.read(new Sheet(sheetNo, headLineNum, rowModel));
        return getExtendsBeanList(excelListener.getDataList(), rowModel);
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     * 自定义WriterHandler 可以定制行列数据进行灵活化操作
     *
     * @param response HttpServletResponse
     * @param list     数据 list，每个元素为一个 BaseRowModel
     * @param fileName 导出的文件名
     */
    public static <T> void writeExcel(HttpServletResponse response, List<T> list, String fileName, Class<?> clazz) {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setOutputStream(getOutputStream(fileName, response, ExcelTypeEnum.XLSX));
        writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
        ExcelWriter writer = new ExcelWriter(writeWorkbook);

        WriteSheet writeSheet = getWriteSheet(1, fileName, clazz);

        try {
            writer.write(list, writeSheet);
        } finally {
            writer.finish();
        }

    }

    /**
     * 导出 Excel ：多个 sheet，带表头
     *
     * @param response  HttpServletResponse
     * @param list      数据 list，每个元素为一个
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     */
    public static ExcelWriterFactory writeExcelWithSheets(HttpServletResponse response, List<?> list,
                                                          String fileName, String sheetName, Class<?> clazz) {

        WriteWorkbook writeWorkbook = getWriteWorkbook(response, fileName);
        ExcelWriterFactory writer = new ExcelWriterFactory(writeWorkbook);

        WriteSheet writeSheet = getWriteSheet(1, sheetName, clazz);
        writer.write(list, writeSheet);
        return writer;
    }

    /**
     * 生成excel文件并下载到本地
     *
     * @param list
     * @param fileName  excel文件名
     * @param sheetName
     * @param clazz
     * @return
     */
    public static ExcelWriterFactory writeExcelWithSheetsAndDownload(List<?> list, String fileName, String sheetName, Class<?> clazz) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(FileConstant.TMP_EXCEL + fileName);
            WriteWorkbook writeWorkbook = getWriteWorkbook(fileOutputStream);

            ExcelWriterFactory writer = new ExcelWriterFactory(writeWorkbook);

            WriteSheet writeSheet = getWriteSheet(1, sheetName, clazz);
            writer.write(list, writeSheet);
            return writer;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static WriteWorkbook getWriteWorkbook(HttpServletResponse response, String fileName) {
        return getWriteWorkbook(getOutputStream(fileName, response, ExcelTypeEnum.XLSX));
    }

    private static WriteWorkbook getWriteWorkbook(OutputStream outputStream) {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setOutputStream(outputStream);
        writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
        writeWorkbook.setAutoCloseStream(true);
        writeWorkbook.setAutoTrim(true);
        writeWorkbook.setNeedHead(true);
        return writeWorkbook;
    }

    public static WriteSheet getWriteSheet(int sheetNo, String sheetName, Class<?> clazz) {
        return EasyExcel.writerSheet(sheetNo, sheetName).needHead(true).head(clazz).build();
    }

    /**
     * 导出文件时为Writer生成OutputStream
     */
    private static OutputStream getOutputStream(String fileName, HttpServletResponse response, ExcelTypeEnum excelTypeEnum) {
        //创建本地文件
        String filePath = fileName + excelTypeEnum.getValue();
        try {
            fileName = new String(filePath.getBytes(), "ISO-8859-1");
            response.addHeader("Content-Disposition", "filename=" + fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            throw new ServiceException("创建文件失败！");
        }
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel         需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     */
    private static ExcelReader getReader(MultipartFile excel,
                                         ExcelListener excelListener) {
        if (excel == null) {
            throw new ServiceException("请选择需要上传的文件！");
        }
        String fileName = excel.getOriginalFilename();
        if (fileName == null) {
            throw new ServiceException("文件格式错误！");
        }
        if (!fileName.toLowerCase().endsWith(ExcelTypeEnum.XLS.getValue()) && !fileName.toLowerCase().endsWith(ExcelTypeEnum.XLSX.getValue())) {
            throw new ServiceException("文件格式错误！");
        }
        InputStream inputStream;
        try {
            inputStream = excel.getInputStream();
            return new ExcelReader(new BufferedInputStream(inputStream), null, excelListener, false);
        } catch (IOException e) {
            throw new ServiceException("解析文件失败！");
        }
    }

    /**
     * 利用BeanCopy转换list
     */
    private static <T extends BaseRowModel> List<T> getExtendsBeanList(List<?> list, Class<T> typeClazz) {
        return MyBeanCopy.convert(list, typeClazz);
    }
}
