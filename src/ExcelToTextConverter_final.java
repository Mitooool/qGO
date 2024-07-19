import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;

public class ExcelToTextConverter_final {

    public static void main(String[] args) {

    }

    public static void run(String excelFilePath) {
        String textFilePath = "E:\\IDEA\\IntelliJ IDEA 2023.2.1\\project\\JDBC\\src\\test\\数据对比\\数据格式处理\\一次处理格式"; // 替换为你要保存数据的文本文件路径

        String inputFilePath = "E:\\IDEA\\IntelliJ IDEA 2023.2.1\\project\\JDBC\\src\\test\\数据对比\\数据格式处理\\一次处理格式";
        String outputFilePath = "E:\\IDEA\\IntelliJ IDEA 2023.2.1\\project\\JDBC\\src\\test\\数据对比\\数据格式处理\\二次处理格式";

        try {
            FileInputStream fis = new FileInputStream(new File(excelFilePath));
            Workbook workbook = new XSSFWorkbook(fis);

            FileWriter writer = new FileWriter(textFilePath);

            DecimalFormat decimalFormat = new DecimalFormat("#"); // 指定不保留小数位

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = getCellValueAsString(cell, decimalFormat);
                        writer.write(cellValue + "\t"); // 使用制表符分隔单元格数据
                    }
                    writer.write("\n"); // 换行以分隔行数据
                }
            }

            writer.close();
            fis.close();
            System.out.println("数据已从Excel成功写入文本文件: " + textFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        processTextFile(inputFilePath, outputFilePath);

        System.out.println("文本文件处理完成！");
    }

    public static void processTextFile(String inputFilePath, String outputFilePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            FileWriter writer = new FileWriter(outputFilePath);

            String line;
            boolean isFirstLine = true; // 用于追踪第一行，避免在开头多余的换行符
            while ((line = reader.readLine()) != null) {
                // 检查是否是空行，如果是，跳过不写入
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 检查行末尾是否是制表符，如果是，就忽略
                if (line.endsWith("\t")) {
                    line = line.substring(0, line.length() - 1); // 去掉末尾的制表符
                }

                // 替换制表符为逗号
                line = line.replace("\t", ",");

                // 在每行前面加上">"
                line = ">" + line;

                // 检查是否包含数字，并替换第一个逗号为回车
                line = replaceFirstCommaWithNewline(line);

                // 写入处理后的行到输出文件，末尾不加换行符
                if (isFirstLine) {
                    isFirstLine = false;
                } else {
                    writer.write("\n"); // 只在非第一行加换行符
                }
                writer.write(line);
            }
            // 关闭文件读取和写入
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String replaceFirstCommaWithNewline(String line) {
        char[] chars = line.toCharArray();
        boolean foundDigit = false;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                foundDigit = true;
            } else if (foundDigit && chars[i] == ',') {
                chars[i] = '\n';
                break;
            }
        }
        return new String(chars);
    }

    private static String getCellValueAsString(Cell cell, DecimalFormat decimalFormat) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return decimalFormat.format(cell.getNumericCellValue()); // 格式化为不保留小数位的字符串
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}