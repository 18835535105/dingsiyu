package com.zhidejiaoyu.common.utils.excelUtil.easyexcel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteWorkbook;

import java.io.IOException;
import java.io.OutputStream;

public class ExcelWriterFactory extends ExcelWriter {
    private OutputStream outputStream;
    private int sheetNo = 1;

    ExcelWriterFactory(WriteWorkbook writeWorkbook) {
        super(writeWorkbook);
        this.outputStream = writeWorkbook.getOutputStream();
    }

    @Override
    public void finish() {
        super.finish();
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
