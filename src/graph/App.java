package graph;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class App {

        public static void main(String[] args) throws IOException, TasteException {

                DataModel model = new FileDataModel(new File("/Users/theocean154/Documents/School_files/College/Programs/eclipse/bda/src/data/test.csv"));
                UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);

                UserNeighborhood neighborhood = new NearestNUserNeighborhood(3,
                                userSimilarity, model);

                Recommender recommender = new GenericUserBasedRecommender(model,
                                neighborhood, userSimilarity);

                Recommender cachingRecommender = new CachingRecommender(recommender);

                List<RecommendedItem> recommendations = cachingRecommender.recommend(
                                1000000000000006075L, 10);

                System.out.println(recommendations);

        }

}