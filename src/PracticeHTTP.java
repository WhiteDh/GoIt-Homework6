import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class PracticeHTTP {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";


    public String createNewUser() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(Files.readAllBytes(new File("createUser.json").toPath()));
        outputStream.close();


        //у відповідь на JSON з об'єктом повернувся такий самий JSON,
        // але зі значенням id більшим на 1, ніж найбільший id на сайті
        soutResponse(connection);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
            System.out.println("succesful created");
            return connection.getResponseMessage();
        }
        System.out.println("failed to create user");
        return connection.getResponseMessage();
    }


    public String UpdateUser(int id) throws IOException {
        URL url = new URL(BASE_URL + "/" + id);

        //перегляд юзера до змін
        HttpURLConnection connectionGET = (HttpURLConnection) url.openConnection();
        connectionGET.setRequestMethod("GET");

        System.out.println("user before updatind");

        soutResponse(connectionGET);


        //зміна юзера
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(Files.readAllBytes(new File("updateUser.json").toPath()));
        outputStream.close();

        //перегляд змін
        soutResponse(connection);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("updating successful");
            return connection.getResponseMessage();
        }
        System.out.println("failed to update user");
        return connection.getResponseMessage();
    }

    private void soutResponse(HttpURLConnection connectionGET) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connectionGET.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            System.out.println(response.toString());
        }
    }


    public void deleteUser(int id) throws IOException {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Accept", "application/json");
        int response = connection.getResponseCode();
        if (response >= 200 && response < 300) {
            System.out.println("user deleted successfully");
        } else {
            System.out.println("failed to delete user");
        }

    }


    public void getAllUsers() throws IOException {
        URL url = new URL(BASE_URL);
        readResponse(url);
    }


    public void getUserById(int id) throws IOException {
        URL url = new URL(BASE_URL + "/" + id);
        readResponse(url);
    }

    public void getUserByName(String name) throws IOException {
        URL url = new URL(BASE_URL + "?username=" + name);
        readResponse(url);
    }

    private void readResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("response code is " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");

            }
            in.close();
            System.out.println(content.toString());

        } else {
            System.out.println(connection.getResponseMessage());
        }
    }


    public void getCommentsForLastPost(int userId) throws IOException {
        URL postsUrl = new URL(BASE_URL + "/" + userId + "/posts");
        HttpURLConnection connection = (HttpURLConnection) postsUrl.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();

            JSONArray postsArray = new JSONArray(response.toString());

            if (postsArray.length() > 0) {
                //bigger id
                JSONObject lastPost = postsArray.getJSONObject(0);
                for (int i = 1; i < postsArray.length(); i++) {
                    JSONObject currentPost = postsArray.getJSONObject(i);
                    if (currentPost.getInt("id") > lastPost.getInt("id")) {
                        lastPost = currentPost;
                    }
                }

                int postId = lastPost.getInt("id");

                // отримання коментраів
                URL commentsUrl = new URL("https://jsonplaceholder.typicode.com/posts/" + postId + "/comments");
                HttpURLConnection commentsConnection = (HttpURLConnection) commentsUrl.openConnection();
                commentsConnection.setRequestMethod("GET");

                if (commentsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader commentsReader = new BufferedReader(new InputStreamReader(commentsConnection.getInputStream()));
                    StringBuilder commentsResponse = new StringBuilder();
                    while ((line = commentsReader.readLine()) != null) {
                        commentsResponse.append(line).append("\n");
                    }
                    commentsReader.close();

                    // парсинг коментарів і запис у файл
                    JSONArray commentsArray = new JSONArray(commentsResponse.toString());
                    String filename = "user-" + userId + "-post-" + postId + "-comments.json";
                    Files.write(Paths.get(filename), commentsArray.toString(4).getBytes(StandardCharsets.UTF_8));
                    System.out.println("Comments for post " + postId + " saved to " + filename);
                } else {
                    System.out.println("Failed to retrieve comments. Response code: " + commentsConnection.getResponseCode());
                }
            } else {
                System.out.println("No posts found for user " + userId);
            }
        } else {
            System.out.println("Failed to retrieve posts. Response code: " + connection.getResponseCode());
        }
    }



    public void getTasks(int userId) throws IOException {
        URL todosUrl = new URL(BASE_URL + "/" + userId + "/todos");
        HttpURLConnection connection = (HttpURLConnection) todosUrl.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();


            JSONArray todosArray = new JSONArray(response.toString());

            System.out.println("tasks for user with id" + userId + ":");

            for (int i = 0; i < todosArray.length(); i++) {
                JSONObject todo = todosArray.getJSONObject(i);
                if (!todo.getBoolean("completed")) {
                    System.out.println("task id: " + todo.getInt("id"));
                    System.out.println("title: " + todo.getString("title"));
                    System.out.println("completed: " + todo.getBoolean("completed") + "\n");

                }
            }
        } else {
            System.out.println("failed to receiv tasks. Code: " + connection.getResponseCode());
        }
    }

}

