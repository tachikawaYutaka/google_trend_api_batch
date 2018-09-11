package com.wakabatimes.google_trend_api_batch;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class GoogleTrendApiBatchApplication implements CommandLineRunner {
    @Autowired
    private BatchConst batchConst;

    private static final Logger logger = LoggerFactory.getLogger(GoogleTrendApiBatchApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GoogleTrendApiBatchApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext context = application.run(args);
        SpringApplication.exit(context);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        logger.info("the start of google trends api batch");
        List<String> argsList = Arrays.asList(args);
        String requestUrl;
        if (argsList.size() == 4) {
            HttpURLConnection  urlConn;
            InputStream in;
            BufferedReader reader;

            String geo = argsList.get(0);
            String ed = argsList.get(1);
            String baseUrl = batchConst.getUrl();
            requestUrl = baseUrl + "?geo=" + geo + "&ed=" + ed;

            //接続するURLを指定する
            URL url = new URL(requestUrl);

            //コネクションを取得する
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.connect();

            int status = urlConn.getResponseCode();
            logger.info("HTTP status:" + status);
            if (status == HttpURLConnection.HTTP_OK) {
                in = urlConn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    if(i > 0) output.append(line);
                    i++;
                }

                //ファイル作成
                String dir = argsList.get(2);
                String fileName = argsList.get(3);

                String filePath = dir + File.separator + fileName;
                File newFile = new File(filePath);
                if(checkBeforeWriteFile(newFile)) {
                    // 文字コードを指定する
                    PrintWriter p_writer = null;
                    try {
                        p_writer = new PrintWriter(new BufferedWriter
                                (new OutputStreamWriter(new FileOutputStream(newFile),"UTF-8")));
                    } catch (UnsupportedEncodingException | FileNotFoundException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                    if (p_writer != null) {
                        p_writer.println(output.toString());
                        p_writer.close();
                    }
                    logger.info("Create " + filePath);
                }else {
                    logger.error("Error Can not write the file");
                }

            }else {
                logger.error("HTTP status:" + status);
            }
        }else {
            logger.error("Incorrect parameters");
        }
    }

    private static boolean checkBeforeWriteFile(File file) {
        return file.exists() && !(!file.isFile() || !file.canWrite());
    }
}
