package com.alibaba.excel.analysis.v07.handlers.sax;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.alibaba.excel.analysis.v07.handlers.CellFormulaTagHandler;
import com.alibaba.excel.analysis.v07.handlers.CellInlineStringValueTagHandler;
import com.alibaba.excel.analysis.v07.handlers.CellTagHandler;
import com.alibaba.excel.analysis.v07.handlers.CellValueTagHandler;
import com.alibaba.excel.analysis.v07.handlers.CountTagHandler;
import com.alibaba.excel.analysis.v07.handlers.HyperlinkTagHandler;
import com.alibaba.excel.analysis.v07.handlers.MergeCellTagHandler;
import com.alibaba.excel.analysis.v07.handlers.RowTagHandler;
import com.alibaba.excel.analysis.v07.handlers.XlsxTagHandler;
import com.alibaba.excel.constant.ExcelXmlConstants;
import com.alibaba.excel.context.xlsx.XlsxReadContext;

/**
 * @author jipengfei
 */
public class XlsxRowHandler extends DefaultHandler {
    private XlsxReadContext xlsxReadContext;
    private static final Map<String, XlsxTagHandler> XLSX_CELL_HANDLER_MAP = new HashMap<String, XlsxTagHandler>(16);

    static {
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_FORMULA_TAG, new CellFormulaTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_INLINE_STRING_VALUE_TAG,
            new CellInlineStringValueTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_TAG, new CellTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_VALUE_TAG, new CellValueTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.DIMENSION, new CountTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.HYPERLINK_TAG, new HyperlinkTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.MERGE_CELL_TAG, new MergeCellTagHandler());
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.ROW_TAG, new RowTagHandler());
    }

    public XlsxRowHandler(XlsxReadContext xlsxReadContext) {
        this.xlsxReadContext = xlsxReadContext;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        XlsxTagHandler handler = XLSX_CELL_HANDLER_MAP.get(name);
        if (handler == null) {
            return;
        }
        xlsxReadContext.xlsxReadSheetHolder().getTagDeque().push(name);
        handler.startElement(xlsxReadContext, name, attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String currentTag = xlsxReadContext.xlsxReadSheetHolder().getTagDeque().peek();
        if (currentTag == null) {
            return;
        }
        XlsxTagHandler handler = XLSX_CELL_HANDLER_MAP.get(currentTag);
        if (handler == null) {
            return;
        }
        handler.characters(xlsxReadContext, ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        XlsxTagHandler handler = XLSX_CELL_HANDLER_MAP.get(name);
        if (handler == null) {
            return;
        }
        handler.endElement(xlsxReadContext, name);
        xlsxReadContext.xlsxReadSheetHolder().getTagDeque().pop();
    }

}
