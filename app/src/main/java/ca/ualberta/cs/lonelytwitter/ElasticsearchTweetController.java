package ca.ualberta.cs.lonelytwitter;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

/**
 * Created by romansky on 10/20/16.
 */
public class ElasticsearchTweetController {

    private static JestDroidClient client = null;


    public static void setClient(){
        if (client ==  null){
            DroidClientConfig config = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080/").build();
            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client  = (JestDroidClient) factory.getObject();
        }
    }


    public static class AddTweetTask extends AsyncTask<Tweet,Void,Void>{
        protected Void doInBackground(Tweet... params){
            setClient();
            Tweet tweet = params[0];
            Index index = new Index.Builder(tweet).index("shaiful-wednesday").type("tweet").build();
            try{
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()){
                    System.out.println("OK");
                    tweet.setTweetId(result.getId());
                }
            }catch (IOException e){
                System.out.println("FFFFUCK");
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class GetTweetsTask extends AsyncTask<String,Void,ArrayList<Tweet>>{
        @Override
        protected ArrayList<Tweet> doInBackground(String... params){
            setClient();
            ArrayList<Tweet> tweets = new ArrayList<Tweet>();

            String query = "{ \"query\": { \"term\" : { \"message\" : \""
                    + params[0] + "\"}}}";
            System.out.println(query);

            Search search = new Search.Builder(query).addIndex("shaiful-wednesday").addType("tweet").build();
            try{
                JestResult result = client.execute(search);
                System.out.println(result.isSucceeded());
                if (result.isSucceeded()){
                    List<NormalTweet> tweetList;
                    tweetList = result.getSourceAsObjectList(NormalTweet.class);
                    System.out.println("-------REsult-------");
                    System.out.println(tweetList);
                    tweets.addAll(tweetList);
                }
            }catch (IOException e){
                Log.d("123","---------------");
                e.printStackTrace();
            }

            return tweets;
        }
    }



}