package couchdblogger.embed;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import couchdblogger.WorkloadLogger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EmbedHttpServer {
    static final int port = 8080;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    private String couchdbHost = "";

    private String logFolder = "/tmp";

    private WorkloadLogger logger;

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public EmbedHttpServer() throws Exception {
        // Also support override the setting with env
        String envCouchdbHost = System.getenv("COUCHDB_HOST");
        if (!StringUtils.isEmpty(envCouchdbHost)) {
            this.couchdbHost = envCouchdbHost;
        }
        String envlogFolder = System.getenv("LOG_FOLDER");
        if (!StringUtils.isEmpty(envlogFolder)) {
            this.logFolder = envlogFolder;
        }

        logger = new WorkloadLogger(this.logFolder);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", httpExchange -> threadPool.execute(() -> {
            try {
                forward(httpExchange);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        server.start();
    }

    protected void copy(HttpResponse from, HttpExchange to) throws IOException {
        for (Header h : from.getAllHeaders()) {
            to.getResponseHeaders().add(h.getName(), h.getValue());
        }
        to.sendResponseHeaders(from.getStatusLine().getStatusCode(), 0);
        IOUtils.copy(from.getEntity().getContent(), to.getResponseBody());
        to.getResponseBody().close();
    }

    protected URI createURI(HttpExchange req) {
        URI uri = req.getRequestURI();
        String path = uri.getRawPath();
        String query = uri.getRawQuery();
        String url = MessageFormat.format("http://{0}{1}", couchdbHost, path);
        if (!StringUtils.isEmpty(query)) {
            url += "?" + query;
        }
        return URI.create(url);
    }

    protected void forward(HttpExchange exchange) throws IOException {
        logger.begin(exchange.getRequestMethod(), exchange.getRequestURI().toString());
        HttpRequestBase forwardReq = createRequest(exchange);
        HttpResponse response = httpclient.execute(forwardReq);
        copy(response, exchange);
        logger.end();
    }

    private HttpRequestBase createRequest(HttpExchange exchange) throws IOException {
        HttpRequestBase to = null;
        switch (exchange.getRequestMethod()) {
            case "GET":
                to = new HttpGet();
                break;
            case "HEAD":
                to = new HttpHead();
                break;
            case "OPTIONS":
                to = new HttpOptions();
                break;
            case "POST":
                to = new HttpPost();
                break;
            case "PUT":
                to = new HttpPut();
                break;
            default:
                throw new IllegalArgumentException("Unknown Method");
        }
        to.setURI(createURI(exchange));
        Headers headers = exchange.getRequestHeaders();
        for (String s : headers.keySet()) {
            if (!s.equalsIgnoreCase("content-length")) {
                to.setHeader(s, headers.get(s).stream().collect(Collectors.joining(" ")));
            }
        }

        if (to instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase eto = (HttpEntityEnclosingRequestBase) to;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            IOUtils.copy(exchange.getRequestBody(), buffer);
            buffer.close();
            eto.setEntity(new ByteArrayEntity(buffer.toByteArray()));
        }
        return to;
    }

    public static void main(String[] args) throws Exception {
        new EmbedHttpServer();
    }
}