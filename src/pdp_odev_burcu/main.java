/**
 * Author: AYŞE BURCU AKSU
 * Number: B211210016
 * Date: 2024-12-01
 */



package pdp_odev_burcu;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class main {
    // Tekli operatörlerin tanımlandığı liste
    private static final List<String> unaryOperators = Arrays.asList("++", "--", "!", "~");

    // İkili operatörlerin tanımlandığı liste
    private static final List<String> binaryOperators = Arrays.asList(
        "<<=", ">>=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
        "==", "!=", ">=", "<=", "&&", "||", "<<", ">>", "+", "-", "*", "/", "%", "&", "|", "^", ">", "<", "="
    );

    // Üçlü operatörün tanımlandığı liste
    private static final List<String> ternaryOperator = Arrays.asList("?");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Kullanıcıdan dosya yolunu al
        System.out.print("Lütfen bir .cpp dosyasının tam yolunu giriniz: ");
        String filePath = scanner.nextLine();

        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Operatör sayacı değişkenleri
            int totalUnary = 0;
            int totalBinary = 0;
            int totalTernary = 0;
            boolean multiLineComment = false;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Boşlukları kaldır

                // Önişlemci yönergelerini (#include, #define) atla
                if (line.startsWith("#")) {
                    continue;
                }

                // Çok satırlı yorum başlangıcı kontrolü
                if (multiLineComment) {
                    if (line.contains("*/")) {
                        multiLineComment = false; // Yorum sonu bulundu
                        line = line.substring(line.indexOf("*/") + 2).trim();
                    } else {
                        continue; // Yorum devam ediyor
                    }
                }
                if (line.startsWith("/*")) {
                    multiLineComment = true; // Yorum başlangıcı bulundu
                    line = line.substring(0, line.indexOf("/*")).trim();
                }

                // Tek satırlık yorumları kaldır
                if (line.contains("//")) {
                    line = line.substring(0, line.indexOf("//")).trim();
                }

                // String ve karakter literallerini kaldır
                line = line.replaceAll("\".*?\"", "").replaceAll("'.*?'", "");

                // Bu satırdaki operatörleri say
                int[] counts = countOperators(line);
                totalUnary += counts[0];
                totalBinary += counts[1];
                totalTernary += counts[2];
            }

            reader.close();

            // Sonuçları ekrana yazdır
            System.out.println("Toplam Tekli Operatör Sayısı: " + totalUnary);
            System.out.println("Toplam İkili Operatör Sayısı: " + totalBinary);
            System.out.println("Toplam Üçlü Operatör Sayısı: " + totalTernary);

        } catch (IOException e) {
            // Hata mesajı
            System.out.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Bir satırdaki operatörlerin sayısını hesaplar.
     * 
     * @param line Satırın içeriği
     * @return Operatör sayıları [tekli, ikili, üçlü]
     */
    private static int[] countOperators(String line) {
        int unaryCount = 0;
        int binaryCount = 0;
        int ternaryCount = 0;

        // Operatörleri uzunluklarına göre sıralayarak eşleşme hatalarını önle
        List<String> allOperators = new ArrayList<>();
        allOperators.addAll(binaryOperators);
        allOperators.addAll(unaryOperators);
        allOperators.addAll(ternaryOperator);
        allOperators.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String op : allOperators) {
            // Operatörün regex karşılığı
            String regex = Pattern.quote(op);
            Matcher matcher = Pattern.compile(regex).matcher(line);

            while (matcher.find()) {
                // Operatör türünü belirle ve sayacı artır
                if (binaryOperators.contains(op)) {
                    binaryCount++;
                } else if (unaryOperators.contains(op)) {
                    unaryCount++;
                } else if (ternaryOperator.contains(op)) {
                    ternaryCount++;
                }
                // Çakışmayı önlemek için eşleşeni değiştir
                line = line.replaceFirst(regex, " ".repeat(op.length()));
            }
        }

        return new int[]{unaryCount, binaryCount, ternaryCount};
    }
}
