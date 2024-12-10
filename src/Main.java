import Utils.GeneProcessUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static class GeneRegion {
        private int number;
        private int start;
        private int end;

        public GeneRegion(int number, int start, int end) {
            this.number = number;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "GeneRegion{" +
                    "number=" + number +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }

        public int getNumber() {
            return number;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

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

    public static void printInOrder(Map<String, Integer> benchmarkIndices, Map<String, Integer> specieScore) {
        // Used to store the output result
        StringBuilder output = new StringBuilder();

        // Sort the keys in specieScore and output the key and value (do not output items with key as "-")
        specieScore.keySet().stream()
                .sorted((s1, s2) -> {
                    int index1 = benchmarkIndices.getOrDefault(s1, Integer.MAX_VALUE);
                    int index2 = benchmarkIndices.getOrDefault(s2, Integer.MAX_VALUE);
                    return Integer.compare(index1, index2);
                })
                .filter(key -> !"-".equals(key)) // Do not output items with key as "-"
                .forEach(key -> {
                    Integer value = specieScore.get(key);
                    if (output.length() > 0) {
                        output.append(","); // Add a comma before the output
                    }
                    output.append(value); // Add the value
                });

        // Output the final result
        System.out.print(output.toString());
    }

    public static void printInOrder2(Map<String, Integer> benchmarkIndices, Map<String, Integer> specieScore) {
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
                    System.out.println(GeneProcessUtils.getOriginalGene(key) + ": " + value);
//                    System.out.println(value);
//                    GeneProcessUtils.writeLineToFile("D:\\IDEA\\IdeaProjects\\JDBC\\src\\test\\数据对比\\结果", key + ": " + value);
                });
    }

    public static void printGeneScoreInOrder(ArrayList<String> benchmark, Map<String, Integer> specieScore) {
        // Create a Map to hold the location of the strings in the benchmark.
        Map<String, Integer> benchmarkIndices = new HashMap<>();
        for (int i = 0; i < benchmark.size(); i++)
            benchmarkIndices.put(benchmark.get(i), i);
        printInOrder2(benchmarkIndices, specieScore);
    }
    public static void printSpecieScoreInOrder(String[] benchmark, Map<String, Integer> specieScore) {
        // Create a Map to hold the location of the strings in the benchmark.
        Map<String, Integer> benchmarkIndices = new HashMap<>();
        for (int i = 0; i < benchmark.length; i++)
            benchmarkIndices.put(benchmark[i], i);
        printInOrder(benchmarkIndices, specieScore);
    }
    public static int getWeight(int genePosition, int benchPosition, List<GeneRegion> geneRegions) {
        int geneRegionIndex = findRegionForPosition(geneRegions, genePosition);
        int benchRegionIndex = findRegionForPosition(geneRegions, benchPosition);

        if (geneRegionIndex == benchRegionIndex) {
            return 1; // If in the same region, return 1
        } else if (Math.abs(geneRegionIndex - benchRegionIndex) == 1 ||
                (geneRegionIndex == 1 && benchRegionIndex == geneRegions.size()) ||
                (geneRegionIndex == geneRegions.size() && benchRegionIndex == 1)) {
            return 2; // If in neighboring region, return 2
        } else {
            return 3; // Not in neighboring region, return 3
        }
    }
    public static int findRegionForPosition(List<GeneRegion> geneRegions, int position) {
        for (int i = 0; i < geneRegions.size(); i++) {
            GeneRegion region = geneRegions.get(i);
            if (position >= region.getStart() && position <= region.getEnd()) {
                return region.getNumber();
            }
        }
        return -1; // If the position is not in any region, return -1
    }
    private static int getGenePosition(String gene, ArrayList<String> benchmark) {
        for (int i = 0; i < benchmark.size(); i++) {
            if (gene.equals(benchmark.get(i))) {
                return i; // Find a match, return index
            }
        }
        String gene1 = gene.charAt(0) == '-' ? gene.substring(1) : "-" + gene;
        for (int i = 0; i < benchmark.size(); i++) {
            if (gene1.equals(benchmark.get(i))) {
                return i; // Find a match, return index
            }
        }
        return -1; // No matches found
    }
    // Get the unsigned gene
    private static String getOriginalGene(String gene) {
        return gene.charAt(0) == '-' ? gene.substring(1) : gene;
    }
    // Determine whether the two genes have been inverted; if they have been inverted, return the unsigned gene; if they have not been inverted, return null.
    private static String isReverse(String s1, String s2) {
        String str1 = getOriginalGene(s1);
        String str2 = getOriginalGene(s2);
        if (str1.equals(str2) && s1.length() != s2.length()) {
            return str1;
        }
        return null;
    }
    // Get the reversed gene
    static String getReverseGene(String gene) {
        return gene.charAt(0) == '-' ? gene.substring(1) : "-" + gene;
    }

    // Score the gene, if it returns a negative value it means that a reversal has occurred
    private static int getScore(String gene, ArrayList<String> benchmark, int j, List<GeneRegion> geneRegions) {
        int score = 0;
        int weight;
        // Compare to the benchmark sequence and add a point if it is different and not reversed.
        if (!gene.equals(benchmark.get(j)) && isReverse(gene, benchmark.get(j)) == null) {
            score++;
        }
        // Determine gene region, if inside region weight = 1, neighboring region weight = 2, not neighboring weight = 3
        int genePosition = j;
        int benchPosition = getGenePosition(gene, benchmark);
        weight = getWeight(genePosition, benchPosition, geneRegions);
        score *= weight;
        // Iterate through the benchmark sequence and determine if there is an inversion to add another point
        for (int k = 0; k < benchmark.size(); k++) {
            if (isReverse(gene, benchmark.get(k)) != null) {
                score++;
                score = -score;
                break;
            }
        }
        return score;
    }

    public static Map<String, Object> getResult(ArrayList<String> benchmark, String species, List<GeneRegion> geneRegions) {
        String[] specie = species.split("\n");
        ArrayList<String> specieList = new ArrayList<>();
        for (String s : specie) {
            if (!s.trim().equals(""))
                specieList.add(s);
        }
        String[] sname = new String[specieList.size() / 2 + 1];
        String[] sval = new String[specieList.size() / 2 + 1];
        for (int i = 0; i < specieList.size(); i++) {
            if (i % 2 == 0)
                sname[i / 2 + 1] = specieList.get(i);
            else
                sval[(i + 1) / 2] = specieList.get(i);
        }

        // Total score for each gene
        Map<String, Integer> geneScore = new HashMap<>();
        // Rearrangement frequency for each gene
        Map<String, Integer> frequency = new HashMap<>();
        // Total score for all genes in each species
        Map<String, Integer> specieScore = new HashMap<>();
        List<Map<String, Integer>> result = new ArrayList<>();

        for (int i = 1; i < sval.length; i++) {
            int num = 0;
            for (int j = 0; j < benchmark.size(); j++) {
                // Compare the i-th gene sequence's j-th gene with the gene at position j; if they are different, add one point
                String[] specieArr = sval[i].split(",");
                // Current gene
                String gene = specieArr[j];
                // Score for the current gene
                int score = getScore(gene, benchmark, j, geneRegions);
                num += Math.abs(score);
            }
            specieScore.put(sname[i], num);
        }
        printSpecieScoreInOrder(sname, specieScore);
        System.out.println();
        return null;
    }

    public static Map<String, Integer> getResult1(ArrayList<String> benchmark, String species, List<GeneRegion> geneRegions) {
        String[] specie = species.split("\n");
        ArrayList<String> specieList = new ArrayList<>();
        for (String s : specie) {
            if (!s.trim().equals(""))
                specieList.add(s);
        }
        String[] sname = new String[specieList.size() / 2 + 1];
        String[] sval = new String[specieList.size() / 2 + 1];
        for (int i = 0; i < specieList.size(); i++) {
            if (i % 2 == 0)
                sname[i / 2 + 1] = specieList.get(i);
            else
                sval[(i + 1) / 2] = specieList.get(i);
        }

        // 每个基因的总分
        Map<String, Integer> geneScore = new HashMap<>();
        // 每个基因的重排频率
        Map<String, Integer> frequency = new HashMap<>();
        // 每个种类中所有基因的总分
        Map<String, Integer> specieScore = new HashMap<>();
        List<Map<String, Integer>> result = new ArrayList<>();

        for (int i = 1; i < sval.length; i++) {
            for (int j = 0; j < benchmark.size(); j++) {
                String[] specieArr = sval[i].split(",");
                // 该基因
                String gene = specieArr[j];
//                if ("176".equals(sname[i])) {
//                    System.out.println(1);
//                }
                if ("-".equals(gene))
                    continue;
                // 基准基因
                String benchGene = benchmark.get(j);
                // 该基因得分
                int score = getScore(gene, benchmark, j, geneRegions);
//                if (score < 0) System.out.println(sname[i] + "--------");
                // 如果有反转key就存储基准序列，没有反转key就存储该基因
                if (score >= 0)
                    geneScore.put(gene, score);
                else geneScore.put(getReverseGene(gene), -score);
                frequency = getUnionMap(geneScore, frequency);
                geneScore.clear();
            }
        }
        printGeneScoreInOrder(benchmark, frequency);
        return frequency;
    }

    /**
     * Reads even-numbered lines from a text file.
     *
     * @param filePath The path to the file from which to read lines.
     * @return A list containing the even-numbered lines from the file.
     */
    public static List<String> readEvenLines(String filePath) {
        List<String> evenLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                // Check if the line number is even
                if (lineNumber % 2 == 0) {
                    evenLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Output exception information
        }

        return evenLines; // Return the even lines
    }
}