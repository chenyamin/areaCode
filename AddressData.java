package cn.stylefeng.guns.modular;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 抓取 * * @author jiangdoc * @date 2019-3-16
 */
public class AddressData {
    public static String SITE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/";
    private static List<RegionEntry> regions = new ArrayList<RegionEntry>();
    private static List<String> urls = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("抓取开始:" + new Date());
        getProvince();
        /*regions.forEach(data->{
            System.err.println(data.toString());
        });*/
        /*System.err.println("-----------------------url--------------------");
        urls.forEach(url->{
            System.err.println(url);
        });*/
        StringBuffer content = new StringBuffer();
        for (RegionEntry one : regions) {
            content.append("insert into ss00_area values('").append(one.getCode().substring(0,6)).append("', '").append(one.getName()).append("', 1  , 100000 , 1 , null , null , null , null);\r\n");
            for (RegionEntry two : one.getSub()) {
                content.append("insert into ss00_area values('").append(two.getCode().substring(0,6)).append("', '").append(two.getName() + "', 2 , '").append(one.getCode().substring(0,6)).append("', 1 , null , null , null , null);\r\n");
                for (RegionEntry three : two.getSub()) {
                    content.append("insert into ss00_area values('").append(three.getCode().substring(0,6)).append("', '").append(three.getName()).append("', 3 , '").append(two.getCode().substring(0,6)).append("', 1 , null , null , null , null );\r\n");
                   /* for (RegionEntry four : three.getSub()) {
                        content.append("insert into sys_town values('").append(one.getCode()).append("', '").append(two.getCode()).append("', '").append(three.getCode()).append("', '").append(four.getCode()).append("','").append(four.getName()).append("', 4 );\r\n");
                    }*/
                }
            }
        }
        FileOutputStream out = null;
        // Region.writeFile(content.toString());
        try {
            out = new FileOutputStream(new File("G:\\log\\city.txt"));
            byte[] bytes = content.toString().getBytes();
            out.write(bytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("抓取完毕:" + new Date());
    }

    private static void getProvince() {
        Document doc;
        try {
            try {
                doc = Jsoup.connect(SITE_URL).post();
            }catch (Exception e){
                doc = Jsoup.connect(SITE_URL).post();
            }
            //Jsoup.connect(SITE_URL).get();
            Elements links = doc.select(".provincetr").select("a");
            RegionEntry region = null;
            for (Element e : links) {
                region = new RegionEntry();
                String href = e.attr("href");
                String[] arr = href.split("\\.");
                String code = arr[0];
                if (arr[0].length() < 6) {
                    for (int i = 0; i < 6 - arr[0].length(); i++) {
                        code += "0";
                    }
                }
                region.setCode(code);
                region.setName(e.text()); // href的绝地路径
                String absHref = e.attr("abs:href");
                System.out.println(absHref);
                getCity(absHref, region);
                regions.add(region);
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取市地址 * @param url * @param region
     */
    private static void getCity(String url, RegionEntry region) {
        Document doc;
        try {
            try {
                doc = Jsoup.connect(url).post(); //Jsoup.connect(url).get().charset(charset); // <tr class='citytr'><td><a href='65/6501.html'>650100000000</a></td><td><a href='65/6501.html'>乌鲁木齐市</a></td></tr>
            }catch (Exception e){
                doc = Jsoup.connect(url).post(); //Jsoup.connect(url).get().charset(charset); // <tr class='citytr'><td><a href='65/6501.html'>650100000000</a></td><td><a href='65/6501.html'>乌鲁木齐市</a></td></tr>
            }
            Elements links = doc.select(".citytr");
            RegionEntry city;
            for (Element e : links) {
                city = new RegionEntry();
                Elements alist = e.select("a");
                Element codeE = alist.get(0);
                Element codeN = alist.get(1);
                String name = codeN.text();
                String code = codeE.text();
                if ("市辖区".equals(name)) {
                    name = region.getName(); //code = region.getCode();
                }
                city.setCode(code);
                city.setName(name);
                String absHref = codeE.attr("abs:href");
                getArea(absHref, city);
                //urls.add(absHref);
                region.getSub().add(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取区县地址 * @param url * @param region
     */
    private static void getArea(String url, RegionEntry region) {
        Document doc;
        try {
            try {
                doc = Jsoup.connect(url).post(); // Jsoup.connect(url).get(); //<tr class='countytr'><td><a href='01/130102.html'>130102000000</a></td><td><a href='01/130102.html'>长安区</a></td></tr>
            }catch (Exception e){
                doc = Jsoup.connect(url).post(); // Jsoup.connect(url).get(); //<tr class='countytr'><td><a href='01/130102.html'>130102000000</a></td><td><a href='01/130102.html'>长安区</a></td></tr>
            }
            Elements links = doc.select(".countytr");
            RegionEntry area;
            for (Element e : links) {
                area = new RegionEntry();
                Elements alist = e.select("a");
                if (alist.size() > 0) {
                    Element codeE = alist.get(0);
                    String code = codeE.text();
                    area.setCode(code);
                    Element codeN = alist.get(1);
                    String name = codeN.text();
                    area.setName(name);
                    String absHref = codeE.attr("abs:href");
                   // getTown(absHref, area);
                    region.getSub().add(area);
                } else {
                    alist = e.select("td");
                    area.setCode(alist.get(0).text());
                    area.setName(alist.get(1).text());
                    region.getSub().add(area);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //乡镇

    private static void getTown(String url, RegionEntry region) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get(); // Jsoup.connect(url).get(); //<tr class='towntr'><td><a href='07/110107001.html'>110107001000</a></td><td><a href='07/110107001.html'>八宝山街道办事处</a></td></tr>
            Elements links = doc.select("tr.towntr");
            RegionEntry town;
            for (Element e : links) {
                town = new RegionEntry();
                Elements alist = e.select("a");
                if (alist.size() > 0) {
                    Element codeE = alist.get(0);
                    String code = codeE.text();
                    town.setCode(code);
                    Element codeN = alist.get(1);
                    String name = codeN.text();
                    town.setName(name);
                    region.getSub().add(town);
                } else {
                    alist = e.select("td");
                    town.setCode(alist.get(0).text());
                    town.setName(alist.get(1).text());
                    region.getSub().add(town);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
