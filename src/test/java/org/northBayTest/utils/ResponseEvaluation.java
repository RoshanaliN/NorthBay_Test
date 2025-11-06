package org.northBayTest.utils;

/*
    Chatgpt used to help develop code on in this java class
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class ResponseEvaluation {

  public static Map<String, Object> evaluateResponse(String expected, String response) {
    Map<String, Object> result = new LinkedHashMap<>();

    // Semantic Word Similarity
    double semanticScore = cosineSimilarity(expected, response);

    // Semantic Sentence Fuzzy Similarity
    double semanticSentenceFuzzyScore = fuzzySentenceCosineSimilarity(expected, response);

    // Format check (basic rule: must start with uppercase, end with .)
    boolean formatCorrect = response.matches("^[A-Z].*\\.$");

    double score = (semanticScore + semanticSentenceFuzzyScore) / 2;

    // Feedback message
    String feedback = score >= 0.75
        ? "Response is semantically aligned."
        : "Response deviates in meaning or detail.";


    result.put("format_correct", formatCorrect);
    result.put("score",score);
    result.put("feedback", feedback);

    return result;
  }

  // --- Cosine Similarity using Lucene EnglishAnalyzer ---
  private static double cosineSimilarity(String text1, String text2) {
    try {
      Map<String, Integer> freq1 = termFreq(text1);
      Map<String, Integer> freq2 = termFreq(text2);

      Set<String> allTerms = new HashSet<>();
      allTerms.addAll(freq1.keySet());
      allTerms.addAll(freq2.keySet());

      double dot = 0, mag1 = 0, mag2 = 0;
      for (String term : allTerms) {
        int v1 = freq1.getOrDefault(term, 0);
        int v2 = freq2.getOrDefault(term, 0);
        dot += v1 * v2;
        mag1 += v1 * v1;
        mag2 += v2 * v2;
      }
      return dot / (Math.sqrt(mag1) * Math.sqrt(mag2));
    } catch (IOException ioException) {
      ioException.printStackTrace();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return 0;
  }

  private static Map<String, Integer> termFreq(String text) throws IOException {
    Map<String, Integer> freq = new HashMap<>();
    Analyzer analyzer = new EnglishAnalyzer();
    TokenStream stream = analyzer.tokenStream(null, text);
    CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
    stream.reset();
    while (stream.incrementToken()) {
      String term = attr.toString();
      freq.put(term, freq.getOrDefault(term, 0) + 1);
    }
    stream.close();
    analyzer.close();
    return freq;
  }

  // --- Cosine similarity based on fuzzy sentence matches ---
  public static double fuzzySentenceCosineSimilarity(String text1, String text2) {
    List<String> sentences1 = extractSentences(text1);
    List<String> sentences2 = extractSentences(text2);

    if (sentences1.isEmpty() || sentences2.isEmpty()) {
      return 0.0;
    }

    double totalSim = 0.0;
    for (String s1 : sentences1) {
      double maxSim = 0.0;
      for (String s2 : sentences2) {
        double sim = jaroWinklerSimilarity(s1, s2);
        if (sim > maxSim) {
          maxSim = sim;
        }
      }
      totalSim += maxSim;
    }

    // Average the similarity across sentences in both directions
    double avg1 = totalSim / sentences1.size();

    totalSim = 0.0;
    for (String s2 : sentences2) {
      double maxSim = 0.0;
      for (String s1 : sentences1) {
        double sim = jaroWinklerSimilarity(s1, s2);
        if (sim > maxSim) {
          maxSim = sim;
        }
      }
      totalSim += maxSim;
    }

    double avg2 = totalSim / sentences2.size();

    // Final similarity is the average of both directional scores
    return (avg1 + avg2) / 2.0;
  }

  // --- Sentence extraction using regex ---
  private static List<String> extractSentences(String text) {
    List<String> sentences = new ArrayList<>();
    String[] parts = text.split("(?<=[.!?])\\s+");
    for (String s : parts) {
      s = s.trim().toLowerCase();
      if (!s.isEmpty()) {
        sentences.add(s);
      }
    }
    return sentences;
  }

  // --- Jaro-Winkler Similarity ---
  private static double jaroWinklerSimilarity(String s1, String s2) {
    if (s1.equals(s2)) {
      return 1.0;
    }

    int[] mtp = matches(s1, s2);
    float m = mtp[0];
    if (m == 0) {
      return 0.0;
    }

    float j = ((m / s1.length() + m / s2.length() + (m - mtp[1]) / m)) / 3;
    float jw = j < 0.7 ? j : j + Math.min(0.1f, 1.0f / mtp[3]) * mtp[2] * (1 - j);
    return jw;
  }

  private static int[] matches(String s1, String s2) {
    String max, min;
    if (s1.length() > s2.length()) {
      max = s1;
      min = s2;
    } else {
      max = s2;
      min = s1;
    }

    int range = Math.max(max.length() / 2 - 1, 0);
    boolean[] matchFlags = new boolean[max.length()];
    int matches = 0;

    for (int i = 0; i < min.length(); i++) {
      char c1 = min.charAt(i);
      for (int j = Math.max(i - range, 0),
          end = Math.min(i + range + 1, max.length());
          j < end; j++) {
        if (!matchFlags[j] && c1 == max.charAt(j)) {
          matchFlags[j] = true;
          matches++;
          break;
        }
      }
    }

    char[] ms1 = new char[matches];
    char[] ms2 = new char[matches];
    int si = 0;
    boolean[] matched2 = new boolean[max.length()];

    for (int i = 0; i < min.length(); i++) {
      char c1 = min.charAt(i);
      for (int j = Math.max(i - range, 0),
          end = Math.min(i + range + 1, max.length());
          j < end; j++) {
        if (!matched2[j] && c1 == max.charAt(j)) {
          ms1[si] = c1;
          matched2[j] = true;
          si++;
          break;
        }
      }
    }

    int transpositions = 0;
    for (int i = 0; i < ms1.length; i++) {
      if (ms1[i] != ms2[i]) {
        transpositions++;
      }
    }

    return new int[]{matches, transpositions / 2, 0, Math.max(s1.length(), s2.length())};
  }

}
