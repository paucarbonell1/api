package me.papaseca;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import me.papaseca.objects.Person;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Main {

    public static HttpServer apiServer;
    public static Database db;

    public static void main(String[] args) throws IOException {
        System.out.println(logger() + "Starting API server...");

        db = new Database();

        apiServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);

        apiServer.createContext("/", new HttpHandler());

        apiServer.start();

        System.out.println(logger() + "API server started on " + apiServer.getAddress());

    }


    public static String logger() {
        String dateFormated = "";

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SS");

        dateFormated += "[" + simpleDateFormat.format(date) + "] ";

        return dateFormated;
    }

    private static class HttpHandler implements com.sun.net.httpserver.HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) {

            String urli = httpExchange.getRequestURI().toString();

            String firstPath = getContext(urli, 1);

            switch (firstPath) {
                case "api":
                    httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                    respondApiRequest(urli, httpExchange);
                    break;
                case "web":
                    respondHttpRequest("<script>\n" +
                            "    var dni = prompt(\"Pon el dni (00000001A)\", \"00000001A\");\n" +
                            "    if (dni != null) {\n" +
                            "        window.location.replace('api/get/' + dni);\n" +
                            "    }\n" +
                            "</script>", httpExchange);
                    break;
                default:
                    respondHttpRequest(urli, httpExchange);

            }

        }
    }

    @SneakyThrows
    public static void respondApiRequest(String urli, HttpExchange httpExchange) {

        String method = getContext(urli, 2);

        switch (method) {
            case "get": {

                String dni = getContext(urli, 3);

                ResultSet resultSet = db.getUserFromDB(dni);

                Person person = null;

                if (resultSet.next()) {
                    person = new Person(resultSet.getString(1), resultSet.getInt(2), resultSet.getString(3), resultSet.getString(4));
                }


                String JSONresponse = "";

                if (person != null) {
                    //USUARIO EXISTE

                    JSONresponse += "{";
                    JSONresponse += "\"name\": \"" + person.getName() + "\",";
                    JSONresponse += "\"age\": \"" + person.getAge() + "\",";
                    JSONresponse += "\"job\": \"" + person.getJob() + "\",";
                    JSONresponse += "\"dni\": \"" + person.getDni() + "\",";
                    JSONresponse += "\"exists\": \"true\"";
                    JSONresponse += "}";
                } else {
                    //USUARIO NO EXISTE

                    JSONresponse += "{";
                    JSONresponse += "\"name\": \"null\",";
                    JSONresponse += "\"age\": \"null\",";
                    JSONresponse += "\"job\": \"null\",";
                    JSONresponse += "\"dni\": \"null\",";
                    JSONresponse += "\"exists\": \"false\"";
                    JSONresponse += "}";


                }
                respondHttpRequest(JSONresponse, httpExchange);

            }

            break;

            case "post": {
                StringBuilder sb = new StringBuilder();
                InputStream ios = httpExchange.getRequestBody();
                int i;
                while (true) {
                    if (!((i = ios.read()) != -1)) break;

                    sb.append((char) i);
                }

                String data = sb.toString();

                Object object = new Gson().fromJson(data, Person.class);
                Person person = (Person) object;

                if (person.getDni() != null && person.getName() != null && person.getJob() != null) {
                    db.addUserToDB(person);
                    respondHttpRequest("post", httpExchange);
                } else {
                    respondHttpRequest("error 500", httpExchange);
                }


            }
            break;
        }
    }

    @SneakyThrows
    public static void respondHttpRequest(String value, HttpExchange httpExchange) {

        byte[] response = value.getBytes();
        httpExchange.sendResponseHeaders(200, response.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response);
        os.close();

    }

    public static String getContext(String value, int pos) {

        String context;

        String[] splitURLI = value.split("/");

        context = splitURLI[pos];

        return context;
    }

}
