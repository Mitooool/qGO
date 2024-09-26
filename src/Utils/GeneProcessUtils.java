package Utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class GeneProcessUtils {
    // 按照指定分隔符组合字符串数组
    public static String combineStringArray(String[] arr, String delimiter) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
    // 获取不带符号的原始基因
    public static String getOriginalGene(String gene) {
        return gene.charAt(0) == '-' ? gene.substring(1) : gene;
    }
    // 去除连续重复基因
    public static String removeDuplicateGenes(String input) {
        StringBuilder result = new StringBuilder();
        String[] genes = input.split("@"); // 按照@符号分割字符串
        String prevGene = "";
        for (int i = 0; i < genes.length; i++) {
            if (!genes[i].equals(prevGene)) { // 如果当前基因名称不等于上一个基因名称
                result.append(genes[i]).append("@"); // 将当前基因名称添加到结果字符串中
                prevGene = genes[i];
            }
        }
        return result.toString().substring(0, result.length() - 1); // 去除最后一个@符号并返回结果字符串
    }
    // 判断所给基因是否在基因数组中
    public static boolean isStringInArray(String[] stringArray, String searchStr) {
        List<String> stringList = new ArrayList<>(Arrays.asList(stringArray));
        return stringList.contains(searchStr);
    }
    // 获取反转后的基因
    public static String getReverseGene(String gene) {
        return gene.charAt(0) == '-' ? gene.substring(1) : "-" + gene;
    }
    // 读取基因文件到字符串数组中
    public static String[] readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines.toArray(new String[0]);
    }
    // 翻转字符串数组
    public static void reverseStringArray(String[] array) {
        int left = 0;
        int right = array.length - 1;

        while (left < right) {
            String temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            left++;
            right--;
        }
    }
    // 读取基因文件中的偶数行到字符串数组中
    public static String[] readEvenLines(String filePath) {
        List<String> evenLines = new ArrayList();
        boolean isEven = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isEven) {
                    evenLines.add(line);
                }
                isEven = !isEven;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evenLines.toArray(new String[0]);
    }
    // 读取基因文件中的奇数行到字符串数组中
    public static String[] readOddLines(String filePath) {
        List<String> evenLines = new ArrayList();
        boolean isEven = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isEven) {
                    evenLines.add(line);
                }
                isEven = !isEven;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evenLines.toArray(new String[0]);
    }
    // 打印输出Map集合
    public static <K, V> void print(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
    // 打印输出Set集合
    public static void print(Set<?> set) {
        for (Object element : set) {
            System.out.println(element);
        }
    }
    // 打印输出List集合
    public static void print(List<?> list) {
        for (Object element : list) {
            if (element instanceof List) {
                print((List<?>) element);
            } else {
                System.out.println(element);
            }
        }
    }
    // 打印输出字符串数组
    public static void print(String[] array) {
        System.out.println(Arrays.toString(array));
    }
    // 将基因频率按照基准序列中基因的顺序输出
    public static void printSpecieScoreInOrder(ArrayList<String> benchmark, Map<String, Integer> specieScore) {
        // 创建一个Map用于存放benchmark中字符串的位置
        Map<String, Integer> benchmarkIndices = new HashMap<>();
        for (int i = 0; i < benchmark.size(); i++) {
            benchmarkIndices.put(benchmark.get(i), i);
        }

        // 对specieScore中的key进行排序并输出key和value（不输出key为"-"的项）
        specieScore.keySet().stream()
                .sorted((s1, s2) -> {
                    int index1 = benchmarkIndices.getOrDefault(s1, Integer.MAX_VALUE);
                    int index2 = benchmarkIndices.getOrDefault(s2, Integer.MAX_VALUE);
                    return Integer.compare(index1, index2);
                })
                .filter(key -> !"-".equals(key)) // 不输出key为"-"
                .forEach(key -> {
                    Integer value = specieScore.get(key);
                    System.out.println(key + ": " + value);
                });
    }
    // 将两个Map集合合并，相同key就把value相加，不相同直接添加
    public static Map<String, Integer> getUnionMap(Map<String, Integer> leftMap, Map<String, Integer> rightMap) {
        if (null != leftMap && null != rightMap) {
            Set<String> leftMapKey = leftMap.keySet();
            Set<String> rightMapKey = rightMap.keySet();
            Set<String> differenceSet = Sets.union(leftMapKey, rightMapKey);

            Map<String, Integer> result = Maps.newHashMap();
            for (String key : differenceSet) {
                if (leftMap.containsKey(key) && rightMap.containsKey(key)) {
                    result.put(key, leftMap.get(key) + rightMap.get(key));
                } else if (leftMap.containsKey(key)) {
                    result.put(key, leftMap.get(key));
                } else {
                    result.put(key, rightMap.get(key));
                }
            }
            return result;
        } else return null;
    }
    // 将两个基因序列合并到一起，如果没有交集就返回null，如果其中一个基因序列为空就返回另一个，两个都为空就返回null
    public static String mergeGenesWithOverlap(String s, String t) {
        if (s == null || s.isEmpty()) {
            return t == null || t.isEmpty() ? null : t;
        } else if (t == null || t.isEmpty()) {
            return s;
        }
        // 将输入的字符串按逗号分割为基因名称数组
        String[] genesS = s.split(",");
        String[] genesT = t.split(",");

        // 将数组转换为List以便进行交集运算
        List<String> listS = new ArrayList<>(Arrays.asList(genesS));
        List<String> listT = new ArrayList<>(Arrays.asList(genesT));
        List<String> temp = new ArrayList<>(Arrays.asList(genesS));

        // 计算两个集合的交集
        temp.retainAll(listT);

        // 如果交集非空，则存在公共子集
        if (!temp.isEmpty()) {
            // 判断s是否在t的前面
            int sIndexInT = Arrays.asList(genesT).indexOf(temp.get(0));
            if (sIndexInT == 0) {
                // 如果是，将t连接到s的后面，并去除重复的公共子集
                for (String gene : temp) {
                    listT.remove(gene);
                }
                listS.addAll(listT);
                return String.join(",", listS);
            } else {
                // 否则，将s连接到t的后面，并去除重复的公共子集
                for (String gene : temp) {
                    listS.remove(gene);
                }
                listT.addAll(listS);
                return String.join(",", listT);
            }
        } else {
            // 如果交集为空，返回null
            return null;
        }
    }
    // 不考虑基因前后顺序判断两个基因序列是否完全相同
    public static boolean areGeneSeqEqualNotByOrder(String[] array1, String[] array2) {
        if (array1 == null && array2 == null) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length != array2.length) {
            return false;
        }
        Arrays.sort(array1);
        Arrays.sort(array2);
        return Arrays.equals(array1, array2);
    }
    // 考虑基因前后顺序判断两个基因序列是否完全相同
    public static boolean areGeneSeqEqualByOrder(String[] arr1, String[] arr2) {
        // 检查数组长度是否相同
        if (arr1.length != arr2.length) {
            return false;
        }
        // 逐个比较数组元素
        for (int i = 0; i < arr1.length; i++) {
            if (!arr1[i].equals(arr2[i])) {
                return false; // 如果有不同的元素，返回false
            }
        }
        // 如果数组的长度和所有元素都相同，返回true
        return true;
    }
    // 判断两个文件的内容是否完全相同
    public static boolean compareFiles(String filePath1, String filePath2) {
        try {
            BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
            BufferedReader reader2 = new BufferedReader(new FileReader(filePath2));

            int lineNumber = 1;

            while (true) {
                String line1 = reader1.readLine();
                String line2 = reader2.readLine();

                // 检查是否两个文件都到达文件末尾
                if (line1 == null && line2 == null) {
                    break;
                }

                // 检查是否两个文件在同一行有差异
                if ((line1 == null && line2 != null) || (line1 != null && line2 == null)) {
                    System.out.println("两个文件在第 " + lineNumber + " 行行数不同：");
                    System.out.println("文件1 行数：" + (line1 == null ? "文件结束" : lineNumber));
                    System.out.println("文件2 行数：" + (line2 == null ? "文件结束" : lineNumber));
                    return false;
                }

                // 如果行内容不同，输出差异并返回false
                if (!line1.equals(line2)) {
                    System.out.println("第 " + lineNumber + " 行不同:");
                    System.out.println("文件1: " + line1);
                    System.out.println("文件2: " + line2);
                    return false;
                }

                lineNumber++;
            }

            // 关闭文件读取器
            reader1.close();
            reader2.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false; // 如果发生异常也返回false
        }

        // 如果程序执行到这里，说明两个文件的内容完全相同
        return true;
    }
    // 将给定的Excel表格中的数据读取到给定的文本文件中
    public static void convertExcelToText(String excelFilePath, String outputFilePath) {
        try {
            FileInputStream excelFile = new FileInputStream(new File(excelFilePath));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheetAt(0);

            File outputFile = new File(outputFilePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            DecimalFormat decimalFormat = new DecimalFormat("0");

            int lastRowNum = sheet.getLastRowNum();

            for (int i = 0; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);

                if (row != null) {
                    boolean rowHasData = false;
                    StringBuilder rowData = new StringBuilder();

                    for (Cell cell : row) {
                        String cellValue = getCellValueAsString(cell, decimalFormat);
                        if (!cellValue.isEmpty()) {
                            rowHasData = true;
                        }
                        rowData.append(cellValue).append("\t");
                    }

                    if (rowHasData) {
                        writer.write(rowData.toString().trim());
                    }

                    if (i < lastRowNum) {
                        writer.newLine();
                    }
                }
            }
            writer.close();
            System.out.println("Text file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 将Excel单元格的值转换为字符串格式并返回
    public static String getCellValueAsString(Cell cell, DecimalFormat decimalFormat) {
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
    // 使基因数组按照最大长度在末尾补-
    public static String[] processStringArray(String[] inputArray) {
        // 找出最长的字符串长度
        int maxLength = 0;
        String[] split;
        for (String str : inputArray) {
            split = str.split(",");
            maxLength = split.length > maxLength ? split.length : maxLength;
        }

        // 将数组中的每个字符串处理成相同长度
        String[] processedArray = new String[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            StringBuilder sb = new StringBuilder();
            String str = inputArray[i];
            sb.append(str);
            for (int j = 0; j < maxLength - str.split(",").length; j++) {
                sb.append("-");
                if (j < maxLength - str.split(",").length - 1)
                    sb.append(",");
            }
            processedArray[i] = sb.toString();
        }
        return processedArray;
    }
    // 从第一个Excel文件中移除与第二个Excel文件第一列匹配的行
    public static void removeMatchingRows(String file1Path, String file2Path) throws Exception {
        FileInputStream file1Input = new FileInputStream(file1Path);
        FileInputStream file2Input = new FileInputStream(file2Path);

        Workbook workbook1 = WorkbookFactory.create(file1Input);
        Workbook workbook2 = WorkbookFactory.create(file2Input);

        Sheet sheet1 = workbook1.getSheetAt(0);
        Sheet sheet2 = workbook2.getSheetAt(0);

        for (int i = 0; i <= sheet2.getLastRowNum(); i++) {
            Row row2 = sheet2.getRow(i);
            Cell cell2 = row2.getCell(0); // 假设第二个excel中的第一列

            for (int j = 0; j <= sheet1.getLastRowNum(); j++) {
                Row row1 = sheet1.getRow(j);
                if (row1 == null)
                    continue;
                Cell cell1 = row1.getCell(0); // 假设第一个excel中的第一列

                if (cell1 != null && cell2 != null && cell1.getStringCellValue().equals(cell2.getStringCellValue())) {
                    sheet1.removeRow(row1);
                    break; // 找到匹配的单元格后删除并跳出内循环
                }
            }
        }

        file1Input.close();
        file2Input.close();

        FileOutputStream fileOut = new FileOutputStream(file1Path);
        workbook1.write(fileOut);
        fileOut.close();
    }
    // 对map集合按照value降序
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    // 从指定的Excel文件中获取选定列的数据(表头一起返回)
    public static List<List<String>> getSelectedColumns(String filePath, String sheetName, int[] selectedColumns) {
        List<List<String>> columnsData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);

            for (int columnNum : selectedColumns) {
                List<String> columnData = new ArrayList<>();
                for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    Cell cell = row.getCell(columnNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        String cellValue;
                        if (cell.getCellType() == CellType.NUMERIC.getCode()) {
                            cellValue = String.valueOf(cell.getNumericCellValue());
                        } else {
                            cellValue = cell.getStringCellValue();
                        }
                        columnData.add(cellValue);
                    } else {
                        columnData.add("");
                    }
                }
                columnsData.add(columnData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return columnsData;
    }
    // 将指定的字符串写入到指定的文件中，并在行尾添加换行符
    public static void writeLineToFile(String filePath, String line) {
        try {
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file, true);  // 设置为true表示以追加模式写入文件
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line);
            bw.newLine();  // 写入换行符
            bw.flush(); // 刷新缓冲区，确保数据被写入文件
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 从给定excel表中选取两行读取，生成fasta文件到excel所在地址
    public static void readSelectedColumns(String filePath, String sheetName, String algorithm, int column1Index, int column2Index, int column3Index) throws FileNotFoundException {
        try (FileInputStream file = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheet(sheetName);
            String textFileName = null;
            if (sheet != null) {
                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第二行开始读取，跳过表头
                    Row row = sheet.getRow(i);
                    Cell cell1 = row.getCell(column1Index);
                    Cell cell2 = row.getCell(column2Index);
                    Cell cell3 = row.getCell(column3Index);
                    if (cell1 != null && cell2 != null && cell3 != null) {
                        String cell1Value;
                        String cell2Value;
                        String cell3Value;

                        // 检查单元格数据类型
                        if (cell1.getCellTypeEnum() == CellType.NUMERIC) {
                            cell1Value = String.valueOf(cell1.getNumericCellValue());
                            // 假设 cell1Value 是从数字类型的单元格转换而来的字符串
                            if (cell1Value.contains(".") && cell1Value.endsWith(".0")) {
                                cell1Value = cell1Value.substring(0, cell1Value.indexOf("."));
                            }
                        } else {
                            cell1Value = cell1.getStringCellValue();
                        }

                        if (cell2.getCellTypeEnum() == CellType.NUMERIC) {
                            cell2Value = String.valueOf(cell2.getNumericCellValue());
                            // 假设 cell1Value 是从数字类型的单元格转换而来的字符串
                            if (cell2Value.contains(".") && cell2Value.endsWith(".0")) {
                                cell2Value = cell2Value.substring(0, cell2Value.indexOf("."));
                            }
                        } else {
                            cell2Value = cell2.getStringCellValue();
                        }

                        if (cell3.getCellTypeEnum() == CellType.NUMERIC) {
                            cell3Value = String.valueOf(cell3.getNumericCellValue());
                            // 假设 cell1Value 是从数字类型的单元格转换而来的字符串
                            if (cell3Value.contains(".") && cell3Value.endsWith(".0")) {
                                cell3Value = cell3Value.substring(0, cell3Value.indexOf("."));
                            }
                        } else {
                            cell3Value = cell3.getStringCellValue();
                        }

                        // 如果指定qmgr算法
                        if ("qmgr".equals(algorithm)) {
                            textFileName = filePath.substring(0, filePath.lastIndexOf(".")) + "_qmgr.txt"; // 提取文件名，去除文件后缀
                            GeneProcessUtils.writeLineToFile(textFileName, ">" + cell1Value + "\n" + cell2Value + "," + cell3Value);
                            GeneProcessUtils.writeLineToFile(textFileName, "\n");
                            // 如果指定qgo算法
                        } else if ("qgo".equals(algorithm)) {
                            textFileName = filePath.substring(0, filePath.lastIndexOf(".")) + "_qgo.txt"; // 提取文件名，去除文件后缀
                            GeneProcessUtils.writeLineToFile(textFileName, ">" + cell1Value + "\n" + cell2Value);
                        }
                    }
                    trimTrailingNewlines(textFileName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
        // 去除文件末尾多余的换行符
    public static void trimTrailingNewlines(String filePath) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            long length = raf.length();
            if (length > 0) {
                byte b;
                do {
                    length -= 1;
                    raf.seek(length);
                    b = raf.readByte();
                } while ((b == '\n' || b == '\r') && length > 0);

                final long newLength = length + 1;
                raf.setLength(newLength);
                raf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
