import Utils.GeneProcessUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main_Test {
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
        // Sort the key in specieScore and output the key and value (do not output items with a key of "-")
        specieScore.keySet().stream()
                .sorted((s1, s2) -> {
                    int index1 = benchmarkIndices.getOrDefault(s1, Integer.MAX_VALUE);
                    int index2 = benchmarkIndices.getOrDefault(s2, Integer.MAX_VALUE);
                    return Integer.compare(index1, index2);
                })
                .filter(key -> !"-".equals(key))
                .forEach(key -> {
                    Integer value = specieScore.get(key);
                    System.out.println(GeneProcessUtils.getOriginalGene(key) + ": " + value);
                });
    }

    public static void printGeneScoreInOrder(ArrayList<String> benchmark, Map<String, Integer> specieScore) {
        // Create a Map to hold the location of the strings in the benchmark.
        Map<String, Integer> benchmarkIndices = new HashMap<>();
        for (int i = 0; i < benchmark.size(); i++)
            benchmarkIndices.put(benchmark.get(i), i);
        printInOrder(benchmarkIndices, specieScore);
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
    private static String getReverseGene(String gene) {
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

        Map<String, Integer> geneScore = new HashMap<>();
        Map<String, Integer> frequency = new HashMap<>();
        Map<String, Integer> specieScore = new HashMap<>();
        List<Map<String, Integer>> result = new ArrayList<>();

        for (int i = 1; i < sval.length; i++) {
            for (int j = 0; j < benchmark.size(); j++) {
                String[] specieArr = sval[i].split(",");
                String gene =  specieArr[j];
                if ("-".equals(gene))
                    continue;
                String benchGene = benchmark.get(j);
                // Score for the gene
                int score = getScore(gene, benchmark, j, geneRegions);
                if (score >= 0)
                    geneScore.put(gene, score);
                else geneScore.put(getReverseGene(gene), -score);
                frequency = getUnionMap(geneScore, frequency);
                geneScore.clear();
            }
        }
        System.out.println("Gene:RF");
        printGeneScoreInOrder(benchmark, frequency);

        for (int i = 1; i < sval.length; i++) {
            int num = 0;
            for (int j = 0; j < benchmark.size(); j++) {
                // Compare the jth gene of the ith gene sequence with the gene at the jth position and add a point if it is different
                String[] specieArr = sval[i].split(",");
                String gene = specieArr[j];
                // Score for the gene
                int score = getScore(gene, benchmark, j, geneRegions);
                num += Math.abs(score);
            }
            specieScore.put(sname[i], num);
        }
        System.out.println();
        System.out.println();
        System.out.println("Species:RS");
        printSpecieScoreInOrder(sname, specieScore);
        return null;
    }

    public static void main(String[] args) throws IOException {
        // Address of the fasta file that holds the genetic data
        String fileName = "";
        List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        String benchmarkStr = null;

        // Address of the file where the gene sequence is stored
        String filePath = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                benchmarkStr = line;
                ArrayList<String> benchmark = new ArrayList<>();
                String[] strings = benchmarkStr.split(",");
                for (String string : strings)
                    benchmark.add(string);

                String species = "";
                for (String line1 : lines) {
                    if ('>' == (line1.charAt(0)) && !"".equals(line1)) {
                        species = species + line1.substring(1) + "\n";
                    } else species = species + line1 + "\n";
                }

                List<GeneRegion> geneRegions = new ArrayList<>();
                geneRegions.add(new GeneRegion(1, 0, 50));
                Map<String, Object> result = getResult(benchmark, species, geneRegions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}