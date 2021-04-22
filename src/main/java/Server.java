import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Server {
    public static final String PATH_TO_PROPERTIES = "src/main/resources/param.properties";
    public static int portParam;
    public static SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static void main(String[] args) throws IOException {

        //Читаем файл с параметрами и присваиваем их переменным.
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            portParam = Integer.parseInt(prop.getProperty("port"));
        } catch (IOException e) {
            System.out.println("Ошибка в программе: файл " + PATH_TO_PROPERTIES + " не обнаружено");
            e.printStackTrace();
        }
        //Создаем для прослушивания клиентов
        ServerSocket server = new ServerSocket(portParam);

        //Создается, как только клиент подключился
        Socket soc = server.accept();
        System.out.println("Клиент " + soc.getInetAddress().getHostAddress() + " подключился.");
        //Для чтения сообщений от клиента
        BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        //Для отправки сообщений клиенту
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
        String clientMassage;

        //Бесконечный цикл для того, что бы программа не завершалась после первого же сообщения
        while (true) {
            //читаем сообщение от клиента и сохраняем в переменной
            clientMassage = reader.readLine();
            //Выходим из цикла, если в сообщении присутствует \exit.
            if (clientMassage.contains("\\exit")) {
                break;
            }
            //Создаём JSON объект из сообщения клиента
            JSONObject clientMassageJSON = new JSONObject(clientMassage);

            //Выводим в консоль, что приняли
            System.out.println("Принято сообщение от клиента: " + clientMassageJSON.getJSONObject("Request")
                    .getJSONObject("User").get("Login") + ", отправлено: "
                    + clientMassageJSON.getJSONObject("Request").getJSONObject("Massage").get("Timestamp")
                    + ", текст: " + clientMassageJSON.getJSONObject("Request").getJSONObject("Massage").get("Body"));

            //JSON ответ
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("Response", new JSONObject()
                    .put("Massage", new JSONObject()
                            .put("UserLogin", clientMassageJSON.getJSONObject("Request").getJSONObject("User").get("Login"))
                            .put("Result", "success")
                            .put("Timestamp", formatForDateNow.format(new Date()))));
            writer.write("Сообщение доставлено: " + responseJSON + "\n");
            writer.flush();
        }

        System.out.println("Клиент закрыл соединение.");

    }
}
