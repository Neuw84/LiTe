package edu.ehu.galan.lite.utils.wikiminer;

/*
 * Copyright (C) 2014 Angel Conde Manjon neuw84 at gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import edu.ehu.galan.lite.model.Topic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.sleepycat.je.EnvironmentLockedException;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.utils.Caches;
import edu.ehu.galan.lite.utils.ProgressTracker;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare.GSonCompare;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare.Interpretation;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.ArticleList;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.WikiDataArt;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.DisamList;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Disambiguation;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.DisambiguationDetailsList;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.explore.GSonExplore;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate.Comparison;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate.Comparisons;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listSearch.ListSearch;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.search.Label;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.search.Search;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.search.Sense;
import edu.ehu.galan.lite.utils.yago2.Char;
import gnu.trove.set.hash.TLongHashSet;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikipedia.miner.comparison.ArticleComparer;
import org.wikipedia.miner.comparison.LabelComparer;
import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Category;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.util.NGrammer;
import org.wikipedia.miner.util.NGrammer.NGramSpan;
import org.wikipedia.miner.util.WikipediaConfiguration;
import org.xml.sax.SAXException;

/**
 * Class that helps with the interaction with Wikiminner services
 *
 * @author Angel Conde Manjon
 */
public class WikiminnerHelper {

    private final Cache cache;
    private HttpClient httpClient;
    private final Properties props;
    private String wikiminerUrl;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String lang = "en";
    private int maxTopics = 50;
    private Caches caches;
    private boolean localMode = false;
    private Wikipedia wikipedia;
    private static volatile WikiminnerHelper instance = null;

    private WikiminnerHelper(String pPropDirs) {
        //PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
// Increase max total connection to 200
        // cm.setMaxTotal(100);
// Increase default max connection per route to 20
        // cm.setDefaultMaxPerRoute(20);
// Increase max connections for localhost:80 to 50
        PoolingClientConnectionManager pm = new PoolingClientConnectionManager();
        pm.setDefaultMaxPerRoute(20);
        pm.setMaxTotal(200);
        httpClient = new DefaultHttpClient(pm);
//        ConnectionConfig connectionConfig = ConnectionConfig.custom()
//                .setMalformedInputAction(CodingErrorAction.IGNORE)
//                .setUnmappableInputAction(CodingErrorAction.IGNORE)
//                .setCharset(Consts.UTF_8).build();
//        cm.setDefaultConnectionConfig(connectionConfig);

//        httpClient = HttpClients.custom()
//                .setConnectionManager(cm)
//                .build();
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        cache = CacheManager.getInstance().getCache("LiteCache");
        props = new Properties();
        caches = new Caches();
        try {
            props.load(new FileInputStream(new File(pPropDirs + "lite/configs/general.conf")));
            wikiminerUrl = props.getProperty("serviceUrl");
            maxTopics = Integer.parseInt(props.getProperty("maxTopics"));
        } catch (IOException ex) {
            logger.error("Error while setting WikiminerHelper properties files, check dirs", ex);
        }
        localMode = !props.get("localMode").equals("false");
    }

    public static WikiminnerHelper getInstance(String pPropDirs) {
        if (instance == null) {
            synchronized (WikiminnerHelper.class) {
                if (instance == null) {
                    instance = new WikiminnerHelper(pPropDirs);
                }
            }
        }
        return instance;

    }

    /**
     *
     * @param pPropDirs
     */
    public void setProperties(String pPropDirs) {
        try {
            props.load(new FileInputStream(new File(pPropDirs + "lite/configs/general.conf")));
            wikiminerUrl = props.getProperty("serviceUrl");
            maxTopics = Integer.parseInt(props.getProperty("maxTopics"));
        } catch (IOException ex) {
            logger.error("Error while setting WikiminerHelper properties files, check dirs", ex);
        }

    }

    /**
     * Sets the language if wich wikiminer calls will be used (the default is en)
     *
     * @param pLang
     */
    public void setLanguage(String pLang) {
        lang = pLang;
    }

    /**
     *
     * @param term1
     * @param term2
     * @return
     */
    public Compare compareTopics(String term1, String term2) {
        FileOutputStream st = null;
        Compare comp = new Compare();
        try {
            String ter1 = Char.encodeURIPathComponent(term1);
            String ter2 = Char.encodeURIPathComponent(term2);
            Element elem = cache.get(ter1 + ter2);
            if (elem == null) {
                HttpGet getRequest = new HttpGet(
                        wikiminerUrl + "services/compare?term1=" + ter1.trim() + "&term2=" + ter2.trim() + "&wikipedia=" + lang + "&responseFormat=JSON");
                getRequest.addHeader("accept", "application/json");
                getRequest.addHeader("Accept-Encoding", "gzip");

                HttpResponse response = httpClient.execute(getRequest);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatusLine().getStatusCode());
                }
                Gson son = new GsonBuilder().create();
                GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                GSonCompare ex = son.fromJson(jsonText, GSonCompare.class);
//                System.out.println("###");
                if (ex.getRelatedness() == null) {
                    logger.debug("unrecognized term: {0} {1}", new Object[]{term1, term2});
                    return null;
                }

                List<Interpretation> interList = ex.getDisambiguationDetails().getInterpretations();
                if (interList.isEmpty()) {
                    return null;
                }
                comp.setDisambiguationConfidence(interList.get(0).getDisambiguationConfidence().floatValue());
                comp.setTerm1(interList.get(0).getTitle1());
                comp.setTerm2(interList.get(0).getTitle2());
                comp.setTerm1Id(interList.get(0).getId1());
                comp.setTerm2Id(interList.get(0).getId2());
                comp.setRelatedness(interList.get(0).getRelatedness().floatValue());
                elem = new Element(ter1 + ter2, comp);
                cache.put(elem);
                return (Compare) elem.getObjectValue();

            } else {
                elem = cache.get(ter1 + ter2);
                return (Compare) elem.getObjectValue();
            }
//            System.out.println(comp);
        } catch (ClientProtocolException e) {
            logger.error(null, e);
        } catch (IOException e) {
            logger.error(null, e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException ex) {
                logger.error(null, ex);
            }
        }
        return comp;
    }

    /**
     *
     */
    public void closeConnection() {
        httpClient.getConnectionManager().shutdown();
    }

    /**
     *
     */
    public void openConnection() {

        httpClient = null;
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

    }

    /**
     *
     * @param term1
     * @return
     */
    public Topic searchTopic(String term1) {
        FileOutputStream st = null;
//        System.out.println(term1);
        try {
            String term = Char.encodeURIPathComponent(term1);
            Element elem = cache.get(term);

            if (elem == null) {
                HttpGet getRequest = new HttpGet(
                        wikiminerUrl + "services/search?query=" + term + "&responseFormat=JSON");
                getRequest.addHeader("accept", "application/json");
                getRequest.addHeader("Accept-Encoding", "gzip");

                HttpResponse response = httpClient.execute(getRequest);
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println(term);
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatusLine().getStatusCode());
                }
                Gson son = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                Search ex = son.fromJson(jsonText, Search.class);
                if (ex.getLabels().size() == 1 && ex.getLabels().get(0).getSenses().isEmpty()) {
                    return null;
                } else {
                    Topic top = new Topic(ex.getRequest().getQuery());

                    for (Label lab : ex.getLabels()) {
                        if (!lab.getSenses().isEmpty()) {
                            if (lab.getSenses().size() == 1) {
                                top.setId(lab.getSenses().get(0).getId());
                                top.setSourceTitle(lab.getSenses().get(0).getTitle());
//                                top.addSense(lab.getSenses().get(0).getTitle());
                            }
//                            top.addLabel(lab.getText());
                            for (Sense sen : lab.getSenses()) {
                                top.addSense(sen.getTitle());
                            }
                        }
                    }
//                    System.out.println(term1);
                    elem = new Element(term1, top);
                    cache.put(elem);
                    return top;

                }
            } else {
                return (Topic) elem.getObjectValue();
            }
        } catch (ClientProtocolException e) {
            logger.error(null, e);
        } catch (IOException e) {
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException ex) {
                logger.error(null, ex);
            }
        }
        return null;
    }

    /**
     *
     * @param term1
     * @return
     */
    public int getIdFromTitle(String term1, String pLang) {
        FileOutputStream st = null;
//        System.out.println(term1);
        try {
            String term = Char.encodeURIPathComponent(term1);
            Element elem = cache.get(term);

            if (elem == null) {
                HttpGet getRequest = new HttpGet(
                        wikiminerUrl + "services/exploreArticle?title=" + term + "&responseFormat=JSON&wikipedia=" + pLang);
                getRequest.addHeader("accept", "application/json");
                getRequest.addHeader("Accept-Encoding", "gzip");
                HttpResponse response = httpClient.execute(getRequest);
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println(term);
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatusLine().getStatusCode());
                }
                Gson son = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                String jsonText;
                jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                WikiDataArt ex = son.fromJson(jsonText, WikiDataArt.class);
                List<ArticleList> list = ex.getArticleList();
                if (list.size() > 0) {
                    return list.get(0).getId();

                } else {
                    return -1;
                }
            }
        } catch (IOException | ParseException ex) {
            logger.error("Error while getting and id form title", ex);
        }
        return -1;

    }

    /**
     *
     * @param term1
     * @return
     */
    public Topic validateTopic(String term1) {
        FileOutputStream st = null;
        Topic top = null;
//        System.out.println(term1);
        top = searchTopic(term1);
        if (top == null) {
            return null;
        }
        return top.getSenseList().size() == 1 ? top : null;
    }
    //return (top.getSenseList().size() == 1) ? top.getId() : -1;
    //TODO: In the original code we check the following (if one term has more than one sense but one seems to be the one we accept it
//           while (iter.hasNext()) {
//                Object object = iter.next();
//                if (object instanceof Element) {
//                    Element currentElement = (Element) object;
//                    if (currentElement.getName().equalsIgnoreCase("label")) {
//                        if (currentElement.getAttribute("linkDocCount").getIntValue() > 0) {
//                            List<Element> childs = currentElement.getChildren();
//                            if (childs.size() == 1) {
//                                Element curr = childs.get(0);
//                                top.setWikiId(curr.getAttribute("id").getIntValue());
//                                top.addSense(curr.getAttributeValue("title"));
//                                return true;
//                            } else {
//                                Iterator itr = childs.iterator();
//                                while (itr.hasNext()) {
//                                    Object obj = itr.next();
//                                    if (obj instanceof Element) {
//                                        Element curr = (Element) obj;
//                                        if (curr.getName().equalsIgnoreCase("sense")) {
//                                            if (curr.getAttributeValue("fromRedirect").equalsIgnoreCase("true")) {
//                                                if (curr.getAttributeValue("fromTitle").equalsIgnoreCase("true")) {
//                                                    String probability = curr.getAttributeValue("priorProbability");
//                                                    float prob = Float.parseFloat(probability);
//                                                    if (prob > 0.5) {
//                                                        return true;
//                                                    }
//                                                }
//                                            }
//                                            top.addSense(curr.getAttributeValue("title"));
//
//                                        }
//                                    }
//                                }
//
//                            }
//                            return valid;
    //}

    /**
     *
     * @param wikiId
     * @return
     */
    public List<String> exploreLabels(int wikiId) {
        FileOutputStream out = null;
        try {
            HttpGet getRequest = new HttpGet(
                    //&definitionmaxImageWidth=800&maxImageHeight=600&emphasisFormat=HTML&definitionLenght=LONG
                    wikiminerUrl + "/services/exploreArticle?id=" + wikiId + "&labels&parentCategories&responseFormat=JSON");
            getRequest.addHeader("accept", "application/json");
            getRequest.addHeader("Accept-Encoding", "gzip");

            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            Gson son = new GsonBuilder().create();
            GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
            String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            GSonExplore ex = son.fromJson(jsonText, GSonExplore.class);
            List<String> labelList = new ArrayList<>();
            for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.explore.Label lab : ex.getLabels()) {
                labelList.add(lab.getText());
            }
            System.out.println(wikiId);
            return labelList;
        } catch (ClientProtocolException e) {
            logger.error(null, e);
        } catch (IOException e) {
            logger.error(null, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    logger.error(null, ex);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param termList
     * @return
     */
    public List<Topic> parallelSearch(List<Term> termList) {
        List<String> lis = new ArrayList<>();
        long timeStart = System.nanoTime();
        List<Topic> topicList = new ArrayList<>();
        Gson son = new GsonBuilder().create();
        JsonParser parser = new JsonParser();
        List<Term> uncachedList = new ArrayList<>();
        int i = 0;
        int j = 0, h = 0, x = 0;
        ListSearch ex;
        if (!localMode) {
            try {

                for (Term term : termList) {
                    Element elem = cache.get(term.getTerm());
                    if (elem == null) {
                        uncachedList.add(term);
                    } else {
                        topicList.add((Topic) elem.getObjectValue());
                    }
                }
                logger.info("Mapping step start:");
                ProgressTracker tracker = new ProgressTracker((termList.size() / maxTopics) + 1, "....", this.getClass());
                while (i < uncachedList.size()) {

                    String req = wikiminerUrl + "/services/search?queryList=";
                    String cacheElem = "";
                    int sum = 0;
                    for (; i < termList.size(); i++) {
//                  if(termList.get(i).getTerm().equals("black hole")){
//                      System.out.println("black hole");
//                  }
                        String string = Char.encodeURIPathComponent(termList.get(i).getTerm());
                        if (string.split("%20").length > 6) {
                            logger.debug("An invalid concept has been detected: " + termList.get(i).getTerm());
                        } else {

                            //TODO: DO THIS WELL via customizable file, however the web service is secured now
                            if (!string.equals("%5B") && !string.equals("!") && !string.equals("¡") && !string.equals("%25") && !string.equals("+") && !string.equals("*")
                                    && !string.equals("+%20%2F") && !string.equals("_") && !string.equals("-") && !string.equals(".)") && !string.equals("%5D")
                                    && !string.equals("%5B") && !string.equals(",") && !string.equals(":") && !string.equals(";") && !string.equals(".") && !string.equals("/")
                                    && !string.equals("\\") && !string.equals("%2F") && !string.equals("=") && !string.equals("-") && !string.equals("%3A") && !string.equals("%3B")
                                    && !string.equals("%3E") && !string.equals("%3C") && !string.equals("%3F") && !string.equals("%C2%BF") && !string.equals("~")
                                    && !string.equals("&") && !string.equals("(") && !string.equals(")") && !string.equals(").")) {
                                cacheElem += string;
                                sum++;
                                h++;
                                string = string.replaceAll("[,;:]", "_");
                                req = req + string + ",";
                                if (sum == maxTopics) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!req.substring(req.length() - 1).equals(",")) {
                        req = req.substring(0, req.length() - 1);
                    }
                    HttpGet getRequest = new HttpGet(req + "&wikipedia=" + lang + "&responseFormat=JSON");
                    getRequest.addHeader("accept", "application/json");
                    getRequest.addHeader("Accept-Encoding", "gzip");
                    HttpResponse response = httpClient.execute(getRequest);
                    GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                    Header contentEncoding = response.getFirstHeader("Content-Encoding");
                    if (contentEncoding == null) {
                        EntityUtils.consume(entity);
                        logger.error("Some characters have crashed the call to the web service. \n " + req);
                        logger.error("The response was: " + response.getStatusLine());
                        return topicList;
                    } else if (response.getStatusLine().getStatusCode() == 502) {
                        EntityUtils.consume(entity);

                        logger.error("The proxy has reverted the web call service call (http 502). \n" + req);
                        logger.error("The response was: " + response.getStatusLine());
                        return topicList;
                    }
                    tracker.update();
                    String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    if (jsonText.contains("java.lang.ArrayIndexOutOfBoundsException") || jsonText.contains("\"error\": \"Parameters missing\"")) {
                        logger.error("The current request hash crashed the web service. Check the terms?? " + req);
                    }
                    ex = son.fromJson(jsonText, ListSearch.class);
                    for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listSearch.Label lab : ex.getLabels()) {
                        if (lab.getSenses().isEmpty()) {
//                        lis.add(lab.getText());
//                        System.out.println(lab.getText() + "\tinvalid");
                        } else {
                            Topic top = new Topic(lab.getText());
                            if (!lab.getSenses().isEmpty()) {
                                if (lab.getSenses().size() == 1) {
                                    top.setId(lab.getSenses().get(0).getId());

//                                top.addSense(lab.getSenses().get(0).getTitle());
                                }
                                lab.getSenses().stream().forEach((sen) -> {
                                    top.addSense(sen.getTitle());
                                    top.addSenseId(sen.getId());
                                });

                            }
                            x++;
                            //System.out.println(lab.getText() + "\t" + top.getId() + "\t" + top.getSenseList());
                            topicList.add(top);
                            Element elen = new Element(top.getTopic(), top);
                            cache.put(elen);
                        }

                    }
                    i++;

                    //JsonArray Jarray = parser.parse(output.toString(),Example.class);
                }
//
//            System.out.println(topicList.size()+ " "+x+" "+h);
//            System.exit(222);
//           
                return topicList;
            } catch (IOException ex1) {
                logger.error("Web service call failed... check the config url or your web server status", ex1);
            }
            return topicList;
        } else {
            if (wikipedia != null) {
                logger.info("Mapping step start:");
                ProgressTracker tracker = new ProgressTracker((termList.size()), "Mapping articles....", this.getClass());
                NGrammer nGrammer = new NGrammer(wikipedia.getConfig().getSentenceDetector(), wikipedia.getConfig().getTokenizer());
                float minPriorProb = 0.01F;
                for (Term query : termList) {

                    try {
                        NGramSpan span = nGrammer.ngramPosDetect(query.getTerm())[0];
                        org.wikipedia.miner.model.Label label = wikipedia.getLabel(span, query.getTerm());
                        if (label.getSenses().length > 0) {
                            Topic top = new Topic(query.getTerm());
                            if (label.getSenses().length == 1) {
                                top.setId(label.getSenses()[0].getId());
//                                top.addSense(lab.getSenses().get(0).getTitle());
                            } else {
                                for (org.wikipedia.miner.model.Label.Sense sense : label.getSenses()) {
                                    if (sense.getPriorProbability() < minPriorProb) {
                                        break;
                                    }
                                    top.addSense((sense.getTitle()));
                                    top.addSenseId(sense.getId());
                                }
                                if (!top.getSenseList().isEmpty()) {
                                    topicList.add(top);
                                }
                            }
                        }
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException ex3) {
                        //logger.info("query: " + query.getTerm(), ex3);
                    }
                    tracker.update();

                }
                return topicList;
            } else {
                logger.error("The Wikipedia is not initizalized, call first to localMode method");
                return topicList;
            }
        }
    }

    /**
     *
     * @param pTtopicList
     * @param cGold
     * @param relatedness
     * @param minRelationship
     * @return
     */
    public List<Comparison> parallelRelate(List<Topic> pTtopicList, List<Integer> cGold, float relatedness, int minRelationship) {
        List<Comparison> kust = new ArrayList<>();
        if (!localMode) {
            long timeStart = System.nanoTime();
            List<Topic> topicList = new ArrayList<>();
            Gson son = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            int i = 0;
            List<Integer> intList = new ArrayList<>();
            Comparisons ex = null;
            try {
                ProgressTracker tracker = new ProgressTracker((pTtopicList.size() / maxTopics) + 1, "....", this.getClass());

                while (i < pTtopicList.size()) {
                    String cacheElem = "";
                    String req = wikiminerUrl + "services/compare?ids1=";
                    int sum = 0;
                    for (; i < pTtopicList.size(); i++) {
                        int id = (pTtopicList.get(i).getId());
                        cacheElem += id;
                        sum++;
                        req = req + id + ",";
                        if (sum == maxTopics) {
                            break;
                        }
                    }
                    req = req.substring(0, req.length() - 1);
                    req += "&ids2=";
                    for (Integer gold : cGold) {
                        req = req + gold.toString() + ",";
                        cacheElem += gold.toString();
                    }
                    req = req.substring(0, req.length() - 1);
//                Element elem = cache.get(cacheElem);
//                if (elem == null) {
                    HttpGet getRequest = new HttpGet(req + "&wikipedia=" + lang + "&responseFormat=JSON");
                    getRequest.addHeader("accept", "application/json");
                    getRequest.addHeader("Accept-Encoding", "gzip");
                    HttpResponse response = httpClient.execute(getRequest);
                    GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                    String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    EntityUtils.consume(entity);
                    ex = son.fromJson(jsonText, Comparisons.class);
//                    elem = new Element(cacheElem, ex);
//                    cache.put(elem);
//                } else {
//                    ex = (Comparisons) elem.getObjectValue();
//                }
                    for (Comparison comp : ex.getComparisons()) {
                        if (cGold.contains(comp.getHighId())) {
                            comp.setHighId(null);
                            kust.add(comp);
                        } else {
                            comp.setLowId(comp.getHighId());
                            comp.setHighId(null);
                            kust.add(comp);
                        }

                    }
//                for (Integer id : ex.getIds()) {
//                    intList.add(id);
////                    System.out.println(id);
//                }
//            }
//            for (Integer integer : intList) {
//                for (Topic top : pTtopicList) {
//                    if (top.getId() == integer) {
//                        topicList.add(top);
//                        break;
//                    }
//                }
                    tracker.update();
                }
                long timeEnd = System.nanoTime();
                logger.debug("Parallel Relate processed in: " + ((timeEnd - timeStart) / 1000000) + " for size: " + pTtopicList.size());
                return kust;
            } catch (IOException ex1) {
                logger.error(null, ex1);
            }
            return null;
        } else {
            if (wikipedia != null) {
                ArticleComparer artComparer = null;
                try {
                    artComparer = new ArticleComparer(wikipedia);
                } catch (Exception ex) {
                    logger.error("Error getting article comparer for this wikipedia");
                }
                if (artComparer == null) {
                    logger.error("No comparisons available for this Wikipedia");
                }
                //gather articles from ids1 ;
                TreeSet<Article> articles1 = new TreeSet<>();
                for (Topic id : pTtopicList) {
                    try {
                        Article art = (Article) wikipedia.getPageById(id.getId());
                        articles1.add(art);
                    } catch (Exception e) {
                        //msg.addInvalidId(id.);
                    }
                }

                //gather articles from ids2 ;
                TreeSet<Article> articles2 = new TreeSet<>();
                for (Integer id : cGold) {
                    try {
                        Article art = (Article) wikipedia.getPageById(id);
                        articles2.add(art);
                    } catch (Exception e) {
                        //msg.addInvalidId(id);
                    }
                }
                //if ids2 is not specified, then we want to compare each item in ids1 with every other one
                if (articles2.isEmpty()) {
                    articles2 = articles1;
                }
                TLongHashSet doneKeys = new TLongHashSet();
                float minRelatedness = relatedness;
                //  boolean showTitles = prmTitles.getValue(request);
                for (Article a1 : articles1) {
                    for (Article a2 : articles2) {
                        if (a1.equals(a2)) {
                            continue;
                        }
                        //relatedness is symmetric, so create a unique key for this pair of ids were order doesnt matter 
                        Article min, max;
                        if (a1.getId() < a2.getId()) {
                            min = a1;
                            max = a2;
                        } else {
                            min = a2;
                            max = a1;
                        }
                        //long min = Math.min(a1.getId(), a2.getId()) ;
                        //long max = Math.max(a1.getId(), a2.getId()) ;
                        long key = ((long) min.getId()) + (((long) max.getId()) << 30);
                        if (doneKeys.contains(key)) {
                            continue;
                        }
                        double related = 0;
                        try {
                            related = artComparer.getRelatedness(a1, a2);
                        } catch (Exception ex) {
                        }
                        if (relatedness >= minRelatedness) {
                            Comparison comp = new Comparison();
                            comp.setRelatedness(related);
                            comp.setHighId(max.getId());
                            comp.setLowId(min.getId());
                            if (cGold.contains(comp.getHighId())) {
                                comp.setHighId(null);
                                kust.add(comp);
                            } else {
                                comp.setLowId(comp.getHighId());
                                comp.setHighId(null);
                                kust.add(comp);
                            }
                        }

                        doneKeys.add(key);
                    }
                }

                return kust;
            } else {
                return null;
            }
        }
    }
// private void disambiguateTopic(Topic top) {
//        Compare comp;
//        int count = 0;
//        int lenght = cGold.size();
////        System.out.println("disambiguating: " + top.getTopic());
//    //        for (String gold : cGold) {
//            if (comp != null) {
//                if (comp.getTerm1() != null) {
//                    top.addProbableSense(comp.getTerm1(), comp.getTerm1Id());
//                } else {
//                    count++;
//                }
//            }
//        }
//        if (count == lenght) {
//            top.setDisambiguationFail(true);
//        }
////        System.out.println(top.getGoodSense() + "\t" + top.getWikiId());
//    }

    /**
     *
     * @param pTtopicList
     * @param cGold
     * @return
     */
    public List<Topic> disambiguate(List<Topic> pTtopicList, List<String> cGold) {
        long timeStart = System.nanoTime();
        List<Topic> topicList = new ArrayList<>();
        if (!localMode) {
            Gson son = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            int i = 0;
            HashMap<String, List<List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation>>> disDict = new HashMap<>();
            Disambiguation ex = null;
            String cGolds = cGold.stream().reduce("", (s1, s2) -> s1.concat(s2));
            logger.info("Disambiguation step start:");
            ProgressTracker tracker = new ProgressTracker((pTtopicList.size() / maxTopics) + 1, "....", this.getClass());
            try {
                while (i < pTtopicList.size()) {
                    int sum = 0;
                    String cacheElem = "";
                    String req = wikiminerUrl + "/services/compare?term1List=";
                    for (; i < pTtopicList.size(); i++) {
//                    if(pTtopicList.get(i).getTopic().equalsIgnoreCase("ursa major")){
//                        System.out.println("dsdsadsadsa");
//                    }
                        String string = Char.encodeURIPathComponent(pTtopicList.get(i).getTopic());
                        if (!string.equals("%5B") && !string.equals("!") && !string.equals("¡") && !string.equals("%25") && !string.equals("+") && !string.equals("*")
                                && !string.equals("+%20%2F") && !string.equals("_") && !string.equals("-") && !string.equals(".)") && !string.equals("%5D")
                                && !string.equals("%5B") && !string.equals(",") && !string.equals(":") && !string.equals(";") && !string.equals(".") && !string.equals("/")
                                && !string.equals("\\") && !string.equals("%2F") && !string.equals("=") && !string.equals("-") && !string.equals("%3A") && !string.equals("%3B")
                                && !string.equals("%3E") && !string.equals("%3C") && !string.equals("%3F") && !string.equals("%C2%BF") && !string.equals("~")
                                && !string.equals("&") && !string.equals("(") && !string.equals(")") && !string.equals(").")) {
                            cacheElem += string;
                            req = req + string + ",";
                            sum++;
                            if (sum == maxTopics) {
                                break;
                            }
                        }
                    }
                    req = req.substring(0, req.length() - 1);
                    req += "&term2List=";
                    for (String gold : cGold) {
                        req = req + Char.encodeURIPathComponent(gold) + ",";
                        cacheElem += Char.encodeURIPathComponent(gold);
                    }

                    req = req.substring(0, req.length() - 1);
                    Element elem = cache.get(cacheElem);
                    if (elem == null) {
                        HttpGet getRequest = new HttpGet(req + "&wikipedia=" + lang + "&responseFormat=JSON");
                        getRequest.addHeader("accept", "application/json");
                        getRequest.addHeader("Accept-Encoding", "gzip");
                        HttpResponse response = httpClient.execute(getRequest);
                        GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                        Header contentEncoding = response.getFirstHeader("Content-Encoding");
                        if (contentEncoding == null) {
                            EntityUtils.consume(entity);
                            logger.error("Some characters have crashed the call to the web service. \n " + req);
                            logger.error("The response was: " + response.getStatusLine());

                        } else if (response.getStatusLine().getStatusCode() == 502) {
                            EntityUtils.consume(entity);

                            logger.error("The proxy has reverted the web call service call (http 502). \n" + req);
                            logger.error("The response was: " + response.getStatusLine());
                            return topicList;
                        }
                        tracker.update();
                        String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                        ex = son.fromJson(jsonText, Disambiguation.class);
                        elem = new Element(cacheElem, ex);

                        cache.put(elem);
                    } else {
                        ex = (Disambiguation) elem.getObjectValue();
                    }
                    int x = 0;
                    List<String> terms = Arrays.asList(ex.getRequest().getTerm1List().split(","));
                    int golf = cGold.size();
                    DisamList dis = null;
                    List<List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation>> iList = null;
                    int m = 0;
                    List<DisambiguationDetailsList> disam = ex.getDisambiguationDetailsList();

                    for (DisambiguationDetailsList disambiguationDetailsList : disam) {
                        String term = disambiguationDetailsList.getTerm1();
                        List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation> interpretations = disambiguationDetailsList.getInterpretations();
                        if (disDict.containsKey(term)) {
                            List<List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation>> li = disDict.get(term);
                            li.add(interpretations);
                        } else {
                            List<List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation>> li = new ArrayList<>();
                            li.add(interpretations);
                            disDict.put(term, li);
                        }
                    }
                }

                for (Topic top : pTtopicList) {
                    if (disDict.containsKey(top.getTopic())) {
                        List<List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation>> li = disDict.get(top.getTopic());
                        HashMap<String, List<Double>> count = new HashMap<>();
                        top.initializeSenseCount();
                        for (List<edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation> list : li) {
                            if (list.size() > 0) {
                                edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate.Interpretation in = list.get(0);
                                String tittle = in.getTitle1();
                                int id = in.getId1();
                                double disCong = in.getDisambiguationConfidence();
                                top.addProbableSense(tittle, id, disCong);
                            }
                        }
                        top.getGoodSense();

                    } else {
                        top.setDisambiguationFail(true);
                    }
                }

                long timeEnd = System.nanoTime();
                logger.info("Parallel Disamb processed in: " + ((timeEnd - timeStart) / 1000000) + " for size: " + pTtopicList.size());

                return pTtopicList;
            } catch (IOException ex1) {
                logger.error(null, ex1);
            } catch (com.google.gson.JsonSyntaxException ex2) {
                logger.info("Some Topic crashed the web Service: returning empty list");
                return pTtopicList;
            }
            return null;
        } else {
            if (wikipedia != null) {
                logger.info("Disambiguation step start:");
                ProgressTracker tracker = new ProgressTracker((pTtopicList.size() + 1), "Disambiguation...", this.getClass());

                ArticleComparer artiComparer = null;
                try {
                    artiComparer = new ArticleComparer(wikipedia);
                } catch (Exception ex) {
                    logger.error("Error getting article comparer for this wikipedia");
                }
                if (artiComparer == null) {
                    logger.error("No comparisons available for this Wikipedia");
                }
                LabelComparer lbComparer = null;
                try {
                    lbComparer = new LabelComparer(wikipedia, artiComparer);
                } catch (Exception ex) {
                    logger.error("Error getting label comparer for this wikipedia ");
                }
                if (lbComparer == null) {
                    logger.error("Error getting label comparer for this wikipedia");
                }
                List<org.wikipedia.miner.model.Label> labels = new ArrayList<>();
                NGrammer nGrammer = new NGrammer(wikipedia.getConfig().getSentenceDetector(), wikipedia.getConfig().getTokenizer());
                for (String string2 : cGold) {
                    NGrammer.NGramSpan span2 = nGrammer.ngramPosDetect(string2)[0];
                    org.wikipedia.miner.model.Label lab2 = wikipedia.getLabel(span2, string2);
                    labels.add(lab2);
                }
                List<String> invalidTerm = new ArrayList<>();
                for (Topic string1 : pTtopicList) {
                    //   System.out.println(string1);
                    NGrammer.NGramSpan span = nGrammer.ngramPosDetect(string1.getTopic())[0];
                    org.wikipedia.miner.model.Label lab1 = wikipedia.getLabel(span, string1.getTopic());
                    org.wikipedia.miner.model.Label.Sense[] sen1 = lab1.getSenses();
                    string1.initializeSenseCount();
                    if (sen1.length != 0) {
                        int j = 0;
                        for (org.wikipedia.miner.model.Label lab2 : labels) {
                            org.wikipedia.miner.model.Label.Sense[] sen2 = lab2.getSenses();
                            if (sen2.length != 0) {
                                try {
                                    LabelComparer.ComparisonDetails dets = lbComparer.compare(lab1, lab2);
                                    ArrayList<Interpretation> interpretations = new ArrayList<>();
                                    for (LabelComparer.SensePair sp : dets.getCandidateInterpretations()) {
                                        string1.addProbableSense(sp.getSenseA().getTitle(), sp.getSenseA().getId(), sp.getSenseRelatedness());
                                    }
                                } catch (Exception ex) {
                                    // java.util.logging.Logger.getLogger(WikiminnerHelper.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                            j++;
                        }
                        //System.out.println(string1);
                        string1.getGoodSense();

                    } else {
                        string1.setDisambiguationFail(true);
                    }
                    tracker.update();
                }

                return pTtopicList;

            } else {
                logger.error("The Wikipedia is not initizalized, call first to localMode method");

                return null;
            }

        }
    }

    /**
     *
     * @param pDoc
     * @param links
     */
    public void getData(Document pDoc, boolean links) {
        caches.initializeId2TopicMap(pDoc);
//        for(Document doc: pCorpus.getDocQueue()){
        if (!localMode) {
            HashMap<Integer, Topic> cacheId = caches.getId2TopicMap();
            List<Topic> topicList = pDoc.getTopicList();
            Gson son = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            int i = 0;
            List<Integer> invalidList = new ArrayList<>();
            i = 0;
            WikiDataArt ex;
            try {
                logger.info("Getting Wiki data from the mapped articles:");
                ProgressTracker tracker = new ProgressTracker((topicList.size() / maxTopics) + 1, "....", this.getClass());
                while (i < topicList.size()) {
                    String req = wikiminerUrl + "/services/exploreArticle?ids=";
                    String cacheElem = "";
                    int sum = 0;
                    for (; i < topicList.size(); i++) {
                        int id = (topicList.get(i).getId());
                        cacheElem += id;
//                    if(id==18105){
//                        System.out.println(pTtopicList.get(i).toString());
//                    }
                        req = req + id + ",";
                        sum++;
                        if (sum == maxTopics) {
                            break;
                        }
                    }
                    req = req.substring(0, req.length() - 1);
                    Element elem = cache.get(cacheElem);
                    HttpGet getRequest = null;
                    if (elem == null) {
                        if (links) {
                            getRequest = new HttpGet(req + "&wikipedia=" + lang + "&parentCategories&translations&definition&labels&outLinks&inLinks&linkRelatedness&responseFormat=JSON&responseFormat=JSON");
                        } else {
                            getRequest = new HttpGet(req + "&wikipedia=" + lang + "&parentCategories&translations&definition&labels&responseFormat=JSON&responseFormat=JSON");
                        }

                        getRequest.addHeader("accept", "application/json");
                        getRequest.addHeader("Accept-Encoding", "gzip");
                        HttpResponse response = httpClient.execute(getRequest);
                        GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                        String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                        EntityUtils.consume(entity);
                        ex = son.fromJson(jsonText, WikiDataArt.class);
                        elem = new Element(cacheElem, ex);
                        cache.put(elem);
                    } else {
                        ex = (WikiDataArt) elem.getObjectValue();
                    }
                    List<ArticleList> artiList = ex.getArticleList();
                    int count = 0;
                    for (ArticleList articleList : artiList) {
                        int id = articleList.getId();
                        if (cacheId.containsKey(id)) {
                            Topic top = cacheId.get(id);
                            count++;
                            addInfo2Article(top, articleList, cacheId);
                            //break; if more are disambiguated with the same we get errors
                            //....
                        }
                    }
                    List<Integer> invalids = ex.getInvalidList(); //may containg categories
                    for (Integer integer : invalids) {
                        invalidList.add(integer);
                        if (cacheId.containsKey(integer)) {
                            Topic top = cacheId.get(integer);
                            top.addLabel(top.getTopic());
                            top.addLabel(top.getSourceTitle());
                        }
                    }
                    tracker.update();
                }
                i = 0;

                while (i < invalidList.size()) {
                    int sum = 0;
                    String req = wikiminerUrl + "/services/exploreCategory?ids=";
                    for (; i < invalidList.size(); i++) {

                        int id = invalidList.get(i);
//                    if(id==18105){
//                        System.out.println(pTtopicList.get(i).toString());
//                    }
                        sum++;
                        req = req + id + ",";
                        if (sum == maxTopics) {
                            break;
                        }

                    }
                    req = req.substring(0, req.length() - 1);
                    HttpGet getRequest = new HttpGet(req + "&wikipedia=" + lang + "&parentCategories&translations&definition&labels&responseFormat=JSON");
                    getRequest.addHeader("accept", "application/json");
                    getRequest.addHeader("Accept-Encoding", "gzip");
                    HttpResponse response = httpClient.execute(getRequest);

//                WikiData ex = son.fromJson(response2String(response), WikiData.class);
//                List<ArticleList> artiList = ex.getArticleList();
//                for (ArticleList articleList : artiList) {
//                    int id = articleList.getId();
//                    for (Topic topic : topicList) {
//                        if (topic.getId() == id) {
//                            addInfo2Article(topic, articleList);
//                            break;
//                        }
//                    }
//                }
                }
            } catch (IOException ex1) {
                logger.error(null, ex1);
            }
            //}
        } else {
            if (wikipedia != null) {
                logger.info("Getting Wiki data from the mapped articles:");
                List<Topic> topicList = pDoc.getTopicList();

                List<Integer> validList = new ArrayList<>();
                for (Topic top : topicList) {
                    validList.add(top.getId());
                }
                ProgressTracker tracker = new ProgressTracker((validList.size()) + 1, "Getting data....", this.getClass());
                Integer[] ids = validList.toArray(new Integer[validList.size()]);
                List<Integer> nullList = new ArrayList<>();
                List<Integer> invalidList = new ArrayList<>();
                List<Article> articleList = new ArrayList<>();
                List<Category> catList = new ArrayList<>();
                ArticleComparer artComparer = null;
                try {
                    artComparer = new ArticleComparer(wikipedia);
                } catch (Exception ex) {
                    logger.error("Error getting article comparer for this wikipedia");
                }
                if (artComparer == null) {
                    logger.error("No comparisons available for this Wikipedia");
                }

                for (int i = 0; i < ids.length; i++) {
                    Integer integer = ids[i];
                    org.wikipedia.miner.model.Page pageIds = wikipedia.getPageById(integer);
                    if (pageIds == null) {
                        nullList.add(integer);
                    }
                    switch (pageIds.getType()) {
                        case disambiguation:
                            break;
                        case article:
                            articleList.add((Article) pageIds);
                            break;
                        default:
                            if (pageIds.getType() == org.wikipedia.miner.model.Page.PageType.category) {
                                catList.add((Category) pageIds);
                            } else {
                                nullList.add(integer);
                            }
                    }

                }
                for (Article art : articleList) {
                    Topic top = caches.getId2TopicMap().get(art.getId());
                    top.setIsIndividual(true);

                    String definition = null;
                    definition = art.getFirstParagraphMarkup();
                    top.setSourceDef(definition);
                    if (definition == null) {
                        top.setSourceDef("");
                    }
                    Article.Label[] labels = art.getLabels();
                    int total = 0;
                    for (Article.Label lbl : labels) {
                        total += lbl.getLinkOccCount();
                    }
                    for (Article.Label lbl : labels) {
                        long occ = lbl.getLinkOccCount();
                        if (occ > 0) {
                            top.addLabel(lbl.getText());
                        }
                    }
                    TreeMap<String, String> translations = art.getTranslations();
                    for (Map.Entry<String, String> entry : translations.entrySet()) {
                        top.addTranslation(entry.getKey(), entry.getValue());
                    }
                    Category[] parents = art.getParentCategories();
                    // logger.info("retrieving parents from " + parents.length + " total");
                    for (Category parent : parents) {
                        top.addParentCagegory(parent.getId(), parent.getTitle());
                    }
                    int start = 0;
                    int max = 300;
                    if (max <= 0) {
                        max = Integer.MAX_VALUE;
                    } else {
                        max += start;
                    }
                    if (links) {
                        Article[] linksOut = art.getLinksOut();
                        //logger.info("retrieving out links [" + start + "," + max + "] from " + linksOut.length + " total");
                        for (int i = start; i < max && i < linksOut.length; i++) {
                            if (artComparer != null) {
                                try {
                                    top.addLinkOut(linksOut[i].getId(), artComparer.getRelatedness(art, linksOut[i]));
                                } catch (Exception ex) {
//                                logger.debug("error comparing articles" + ex);
                                }
                            }
                        }
                        start = 0;
                        max = 300;
                        if (max <= 0) {
                            max = Integer.MAX_VALUE;
                        } else {
                            max += start;
                        }
                        Article[] linksIn = art.getLinksIn();
                        // logger.info("retrieving in links [" + start + "," + max + "] from " + linksIn.length + " total");
                        for (int i = start; i < max && i < linksIn.length; i++) {
                            if (artComparer != null) {
                                try {
                                    top.addLinkIn(linksIn[i].getId(), artComparer.getRelatedness(art, linksIn[i]));
                                } catch (Exception ex) {
                                    //    logger.debug("error comparing articles" + ex);
                                }
                            }
                        }
                    }
                    tracker.update();

                }

            }
        }
        caches.clearId2TopicMap();

    }

    private void addInfo2Article(Topic topic, ArticleList articleList, HashMap<Integer, Topic> pIdCache) {
        topic.setIsIndividual(true);

        for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.Label object : articleList.getLabels()) {
            topic.addLabel(object.getText());
        }
        for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.ParentCategory paren : articleList.getParentCategories()) {
            topic.addParentCagegory(paren.getId(), paren.getTitle());
        }
        for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.Translation trans : articleList.getTranslations()) {
            topic.addTranslation(trans.getLang(), trans.getText());
        }
        for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.Link link : articleList.getInLinks()) {
            if (pIdCache.containsKey(link.getId())) {
                topic.addLinkIn(link.getId(), link.getRelatedness());
            }
        }
        for (edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles.Link link : articleList.getOutLinks()) {
            if (pIdCache.containsKey(link.getId())) {
                topic.addLinkOut(link.getId(), link.getRelatedness());
            }
        }
        topic.setSourceDef(articleList.getDefinition());
        topic.setSourceTitle(articleList.getTitle());
        topic.addLabel(topic.getTopic());
        if (topic.getSourceDef() == null) {
            System.out.println("");
        }
    }

    public void closeWikipedia() {
        wikipedia.close();
    }
//    public void getDataTitles(Document doc) {
//        List<Topic> topicList = doc.getTopicList();
//        Gson son = new GsonBuilder().create();
//        JsonParser parser = new JsonParser();
//        int i = 0;
//        int j = 0;
//        List<Integer> invalidList = new ArrayList<>();
//        i = 0;
//        j = 0;
//        WikiDataArt ex = null;
//        for (Topic top : topicList) {
//            try {
//                String req = wikiminerUrl + "/services/exploreArticle?title=" + Char.encodeURIPathComponent(top.getSourceTitle());
//                HttpGet getRequest = new HttpGet(req + "&wikipedia=" + lang + "&parentCategories&translations&definition&labels&responseFormat=JSON");
//                getRequest.addHeader("accept", "application/json");
//                getRequest.addHeader("Accept-Encoding", "gzip");
//                HttpResponse response = httpClient.execute(getRequest);
//                GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
//                String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
//                EntityUtils.consume(entity);
//
//                ex = son.fromJson(jsonText, WikiDataArt.class);
//                List<ArticleList> artiList = ex.getArticleList();
//
//                int count = 0;
//                for (ArticleList articleList : artiList) {
//                    int id = articleList.getId();
//                    addInfo2Article(top, articleList, c);
//                    top.setId(id);
//                    //break; if more are disambiguated with the same we get errors
//                    //....
//
//                }
//                System.out.println(top.getTopic() + "\t" + top.getId() + "\t" + top.getSourceTitle());
//////                List<Integer> invalids = ex.getInvalidList(); //may containg categories
//////                for (Integer integer : invalids) {
//////                    invalidList.add(integer);
//////                    for (Topic topic : topicList) {
//////                        if (topic.getId() == integer) {
//////                            topic.addLabel(topic.getTopic());
//////                            topic.addLabel(topic.getSourceTitle());
//////                            break;
//////                        }
//////                    }
////                }
//            } catch (IOException ex1) {
//                java.util.logging.Logger.getLogger(WikiminnerHelper.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//        }
//    }

    public void setLocalMode(boolean enabled, String configFile) {
        localMode = enabled;
        try {
            WikipediaConfiguration conf = new WikipediaConfiguration(new File(configFile + "-" + lang + ".xml"));
            conf.clearDatabasesToCache();
            wikipedia = new Wikipedia(conf, true);
        } catch (EnvironmentLockedException | ParserConfigurationException | SAXException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            logger.error("Error loading Wikipedia miner Wikipedia, check the config dirs", ex);
        }
    }

    public void setLocalModeWiki(boolean enabled, Wikipedia wiki) {
        localMode = enabled;
        wikipedia = wiki;
    }

    public HashMap<Integer, List<Integer>> getDataTitles(List<Integer> inteList, List<String> topicsEn) {
        HashMap<String, Integer> mapper = new HashMap<>();
        HashMap<Integer, List<Integer>> titles = new HashMap<>();
        for (int i = 0; i < inteList.size(); i++) {
            Integer inte = inteList.get(i);
            String titl = topicsEn.get(i);
            mapper.put(titl, inte);
        }

        HashMap<Integer, List<Integer>> intesList;
        Gson son = new GsonBuilder().create();
        JsonParser parser = new JsonParser();
        int i = 0;
        List<Integer> invalidList = new ArrayList<>();
        WikiDataArt ex;
        try {
            logger.info("Getting Wiki data from the mapped articles:");
            ProgressTracker tracker = new ProgressTracker((topicsEn.size() / maxTopics) + 1, "....", this.getClass());
            while (i < topicsEn.size()) {
                String req = wikiminerUrl + "/services/exploreArticle?titles=";
                String cacheElem = "";
                int sum = 0;
                for (; i < topicsEn.size(); i++) {
                    String string = Char.encodeURIPathComponent(topicsEn.get(i));
                    cacheElem += string;
                    req = req + string + ",";
                    sum++;
                    if (sum == maxTopics) {
                        break;
                    }
                }
                req = req.substring(0, req.length() - 1);
                HttpGet getRequest = null;
                getRequest = new HttpGet(req + "&wikipedia=" + lang + "&parentCategories&responseFormat=JSON");
                getRequest.addHeader("accept", "application/json");
                getRequest.addHeader("Accept-Encoding", "gzip");
                HttpResponse response = httpClient.execute(getRequest);
                GzipDecompressingEntity entity = new GzipDecompressingEntity(response.getEntity());
                String jsonText = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
                ex = son.fromJson(jsonText, WikiDataArt.class);
                List<ArticleList> artiList = ex.getArticleList();
                for (ArticleList articleList : artiList) {
                    int id = articleList.getId();
                    String title = articleList.getTitle();
                    List<Integer> catsIds = articleList.getParentCategories().stream().map(c -> c.getId()).collect(Collectors.toList());
                    catsIds.add(id);
                    Integer origID = mapper.get(title);
                    titles.put(origID, catsIds);
                }
                tracker.update();

            }

        } catch (IOException ex1) {
            logger.error(null, ex1);
        }

        return titles;
    }

}
