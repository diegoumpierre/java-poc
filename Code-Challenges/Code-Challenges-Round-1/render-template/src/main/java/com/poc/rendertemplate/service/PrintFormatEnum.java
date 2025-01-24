package com.poc.rendertemplate.service;

public enum PrintFormatEnum {
    PDF(new PrintPDF()),
    CSV(new PrintCSV()),
    HTML(new PrintHTML());


    private final PrintApplication printApplication;

    PrintFormatEnum(PrintApplication printApplication) {
        this.printApplication = printApplication;
    }

    public PrintApplication getPrintApplication() {
        return printApplication;
    }
}
