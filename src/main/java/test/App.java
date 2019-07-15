package test;

import com.jcraft.jsch.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        testJsoup();

        testJsch();


        System.out.println( "Hello World!" );
    }

    private static void testJsch() {

        JSch jsch=new JSch();

        try {

            Session session=jsch.getSession("root", "vps1", 22);


            jsch.addIdentity("/home/miguel/zzz");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setPassword("adedewd");
            session.setConfig(config);
            //UserInfo ui=new user;
            //session.setUserInfo(ui);
            session.connect();
            System.out.println("Connected");

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand("df -h");
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void testJsoup() {

        Document doc = null;
        try {
            doc = Jsoup.connect("http://en.wikipedia.org/").get();

            log(doc.title());
            Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                log("%s\n\t%s",
                        headline.attr("title"), headline.absUrl("href"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }
}
