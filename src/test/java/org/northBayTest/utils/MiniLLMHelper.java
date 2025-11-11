
package org.northBayTest.utils;

import ai.djl.ModelException;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.huggingface.translator.TextEmbeddingTranslator;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple helper class to measure similarity between two sentences using the MiniLM model from
 * Hugging Face via DJL.
 */
public class MiniLLMHelper {

  private static final Logger logger = LoggerFactory.getLogger(MiniLLMHelper.class);
  private static final String MODEL_ID = "sentence-transformers/all-MiniLM-L6-v2";

  private static ZooModel<String, float[]> model;

  private MiniLLMHelper() {
  }

  /**
   * Calculates how similar two texts are (0 = very different, 1 = identical).
   */
  public static double getSimilarity(String expected, String actual)
      throws IOException, ModelException, TranslateException {

    if (expected == null || actual == null || expected.isBlank() || actual.isBlank()) {
      return 0.0;
    }

    // Load model if not already done
    if (model == null) {
      model = loadModel();
    }

    // Use predictor to get embeddings and compute similarity
    try (Predictor<String, float[]> predictor = model.newPredictor()) {
      float[] expectedVec = predictor.predict(expected);
      float[] actualVec = predictor.predict(actual);
      return cosineSimilarity(expectedVec, actualVec);
    }
  }

  /**
   * Loads the MiniLM model from Hugging Face using DJL.
   */
  private static ZooModel<String, float[]> loadModel() throws IOException, ModelException {
    logger.info("Loading MiniLM model: {}", MODEL_ID);
    HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.newInstance(MODEL_ID);
    TextEmbeddingTranslator translator = TextEmbeddingTranslator.builder(tokenizer).build();

    Criteria<String, float[]> criteria = Criteria.builder()
        .setTypes(String.class, float[].class)
        .optModelUrls("djl://ai.djl.huggingface.pytorch/" + MODEL_ID)
        .optTranslator(translator)
        .build();

    return ModelZoo.loadModel(criteria);
  }

  /**
   * Computes cosine similarity between two float arrays.
   */
  private static double cosineSimilarity(float[] a, float[] b) {
    double dot = 0, normA = 0, normB = 0;
    for (int i = 0; i < a.length; i++) {
      dot += a[i] * b[i];
      normA += a[i] * a[i];
      normB += b[i] * b[i];
    }
    double result = dot / (Math.sqrt(normA) * Math.sqrt(normB));
    if (result > 1.0) {
      result = 1.0;
    } else if (result < -1.0) {
      result = -1.0;
    }
    return result;
  }

  /**
   * Frees model resources
   */
  public static void close() {
    if (model != null) {
      model.close();
      model = null;
      logger.info("MiniLM model closed.");
    }
  }
}