package com.example.bazy2webapp.database;

import com.example.bazy2webapp.database.user.PasswordEncrypter;
import com.example.bazy2webapp.database.user.User;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MongoDBController {
    static String connectionURL;
    static String dbName;
    static MongoClient mongoClient;
    static MongoDatabase db;
    static MongoCollection<Document> col;
    static MongoCollection<Document> colBike;
    static MongoCollection<Document> colBike_User;

    //Łączenie z bazą danych
    public static void connectToDB(){
        connectionURL = "$CONNECTION_URL";
        dbName = "$NAME";
        mongoClient = MongoClients.create(connectionURL);
        db = mongoClient.getDatabase(dbName);
    }
    //region CRUD dla User
    //region Pobieranie informacji na temat użytkowników z bazy danych

    //Zwraca wartośc logiczna w zależności czy użytkownik znajduje sie bazie danych czy nie na podstaiwe nazwy
    public static boolean doesUsernameExistsInDatabase(String username){
        return getUserByUsernameFromDatabase(username) != null;
    }

    //Zwraca listę wszystkich użytkowników z bazy danych
    public static List<User> getAllUsersFromDatabase(){
        col = db.getCollection("userCollection");
        ArrayList<User> users = new ArrayList<>();

        ObjectId id;
        String currentUsername, currentPassword, currentEmail;
        try {
            for (Document document : col.find()) {
                id = (ObjectId) document.get("_id");
                currentUsername = (String) document.get("username");
                currentPassword = (String) document.get("password");
                currentEmail = (String) document.get("email");

                users.add(new User(id, currentUsername, currentPassword, currentEmail));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }

    //Zwraca listę wszystkich nazw użytkowników z bazy danych
    public static List<String> getAllUsernamesFromDatabase(){
        col = db.getCollection("userCollection");
        ArrayList<String> usernames = new ArrayList<>();
        String currentUsername;
        try {
            for (Document document : col.find()) {
                currentUsername = (String) document.get("username");
                usernames.add(currentUsername);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return usernames;
    }

    //Zwraca listę wszystkich emaili użytkowników z bazy danych
    public static List<String> getAllEmailsFromDatabase(){
        col = db.getCollection("userCollection");
        ArrayList<String> emails = new ArrayList<>();
        String currentEmail;
        try {
            for (Document document : col.find()) {
                currentEmail = (String) document.get("email");
                emails.add(currentEmail);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return emails;
    }

    //Zwraca użytkownika na podstawie podanej nazwy użytkownika
    public static User getUserByUsernameFromDatabase(String username){
        connectToDB();
        col = db.getCollection("userCollection");
        List<String> allUsernames =  getAllUsernamesFromDatabase();
        if(!allUsernames.contains(username)) return null;
        else{
            List<User> allUsers = getAllUsersFromDatabase();
            for (User user: allUsers) {
                if(user.getUsername().equals(username)) return user;
            }
        }
        return null;
    }
    //endregion
    //region Zmiana informacji użytkowników z bazy danych

    //Zmiana nazwy użytkownika
    public static void updateUsersUsername(String currentUsername, String newUsername, HttpSession session){
        connectToDB();
        col = db.getCollection("userCollection");
        System.out.println(col.updateOne(Filters.eq("username", currentUsername), Updates.set("username", newUsername)));
        session.setAttribute("username", newUsername);
        User user = getUserByUsernameFromDatabase(newUsername);

        //Szyfrujemy od nowa hasło (ponieważ mamy nowy klucz)
        try {
            String currentDecryptedPassword = PasswordEncrypter.decrypt(user.getPassword(), currentUsername);
            String newEncryptedPassword = PasswordEncrypter.encrypt(currentDecryptedPassword, newUsername);
            col.updateOne(Filters.eq("username", newUsername), Updates.set("password", newEncryptedPassword));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    //Zmiana hasła użytkownika
    public static void updateUsersPassword(String currentUsername, String newPassword){
        connectToDB();
        col = db.getCollection("userCollection");
        try {
            String encryptedPassword = PasswordEncrypter.encrypt(newPassword, currentUsername);
            System.out.println(col.updateOne(Filters.eq("username", currentUsername), Updates.set("password", encryptedPassword)));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    //Zmiana emaila użytkownika
    public static void updateUsersEmail(String currentUsername, String newEmail){
        connectToDB();
        col = db.getCollection("userCollection");
        System.out.println(col.updateOne(Filters.eq("username", currentUsername), Updates.set("email", newEmail)));
    }
    //endregion

    //Usuwa użytkownikaz bazy danych
    public static void deleteUserFromDatabase(String username){
        connectToDB();
        col = db.getCollection("userCollection");
        System.out.println(col.deleteOne(Filters.eq("username", username)));
    }
    //Dodaje użytkownika do bazy danych
    public static void addUserToDatabase(String username, String password, String email) throws NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        connectToDB();
        col = db.getCollection("userCollection");
        Document userDocument = new Document("_id", new ObjectId());
        userDocument.append("username", username)
                    .append("password", PasswordEncrypter.encrypt(password, username))
                    .append("email", email);
        col.insertOne(userDocument);
    }
    //endregion

    //region CRUD dla Bike
    //Zwraca listę wszystkich rowerów z bazy danych
    public static List<Bike> getAllBikesFromDatabase(){
        connectToDB();
        colBike = db.getCollection("bikeCollection");
        ArrayList<Bike> bikes = new ArrayList<>();

        ObjectId id;
        String currentBrand, currentModel, currentImgUrl, currentType;
        int currentWheelSize;
        boolean currentTaken;
        try {
            for (Document document : colBike.find()) {
                id = (ObjectId) document.get("_id");
                currentBrand = (String) document.get("brand");
                currentModel = (String) document.get("model");
                currentImgUrl = (String) document.get("img_url");
                currentType = (String) document.get("type");
                currentTaken = (boolean) document.get("taken");
                currentWheelSize = (int) document.get("wheel_size");
                bikes.add(new Bike(id, currentBrand, currentModel, currentWheelSize, currentTaken, currentImgUrl, currentType));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return bikes;
    }

    //Zwraca wszystkie wypożyczone przez użytkownika rowery
    public static List<Bike> getAllRentedBikesFromDatabase(String username){
        connectToDB();
        colBike = db.getCollection("bikeCollection");
        colBike_User = db.getCollection("bike-userCollection");
        ArrayList<Bike> bikes = new ArrayList<>();
        ArrayList<String> bikesIds = new ArrayList<>();
        User user = MongoDBController.getUserByUsernameFromDatabase(username);
        String userId = String.valueOf(user.getId());

        for(Document document : colBike_User.find(Filters.eq("userId", userId))) {
            bikesIds.add((String) document.get("bikeId"));
        }
        for(String bikeId : bikesIds){
            bikes.add(getBikeByIdFromDatabase(new ObjectId(bikeId)));
        }

        return bikes;
    }

    //Zwraca rower po podaniu id
    public static Bike getBikeByIdFromDatabase(ObjectId id){
        colBike = db.getCollection("bikeCollection");
        Document document = colBike.find(Filters.eq("_id", id)).first();
        String brand = (String) document.get("brand");
        String model = (String) document.get("model");
        String imgUrl = (String) document.get("img_url");
        String type = (String) document.get("type");
        boolean taken = (boolean) document.get("taken");
        int wheelSize = (int) document.get("wheel_size");
        return new Bike(id, brand, model, wheelSize, taken, imgUrl, type);
    }

    //Zmienia pola 'taken' roweru
    public static void updateBikeTakenField(ObjectId id, boolean taken){
        connectToDB();
        colBike = db.getCollection("bikeCollection");
        System.out.println(colBike.updateOne(Filters.eq("_id", id), Updates.set("taken", taken)));
    }

    //Zwraca posortowaną listę rowerów na podstawie wartości string
    public static List<Bike> getSortedBikeList(String typeOfSort, HttpSession session){
        connectToDB();
        colBike = db.getCollection("bikeCollection");
        ArrayList<Bike> bikes = new ArrayList<>();

        //ustalamy warunek sortowania
        String condition;
        Bson bson;
        int alphabeticalSortCounter = (int) session.getAttribute("alphabeticalSortCounter");
        int wheelSizeSortCounter = (int) session.getAttribute("wheelSizeSortCounter");
        switch (typeOfSort){
            case "alphabetical":
                condition = "brand";
                if(alphabeticalSortCounter % 2 == 0) bson = Sorts.ascending(condition);
                else bson = Sorts.descending(condition);
                session.setAttribute("alphabeticalSortCounter", ++alphabeticalSortCounter);
                break;
            case "wheel_size":
                condition = "wheel_size";
                if(wheelSizeSortCounter % 2 == 0) bson = Sorts.ascending(condition);
                else bson = Sorts.descending(condition);
                session.setAttribute("wheelSizeSortCounter", ++wheelSizeSortCounter);
                break;
            default:
                condition = null;
                bson = null;
                break;
        }

        ObjectId id;
        String currentBrand, currentModel, currentImgUrl, currentType;
        int currentWheelSize;
        boolean currentTaken;
        try {
            for (Document document : colBike.find().sort(bson)) {
                id = (ObjectId) document.get("_id");
                currentBrand = (String) document.get("brand");
                currentModel = (String) document.get("model");
                currentImgUrl = (String) document.get("img_url");
                currentType = (String) document.get("type");
                currentTaken = (boolean) document.get("taken");
                currentWheelSize = (int) document.get("wheel_size");
                bikes.add(new Bike(id, currentBrand, currentModel, currentWheelSize, currentTaken, currentImgUrl, currentType));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return bikes;
    }
    //endregion

    //region CRUD dla Bike-User
    //Dodaje relacje do kolekcji bike-user
    public static void addBikeUserRelation(String bikeId, String userId){
        colBike_User = db.getCollection("bike-userCollection");
        Document userDocument = new Document("_id", new ObjectId());
        userDocument.append("bikeId", bikeId)
                .append("userId", userId);
        colBike_User.insertOne(userDocument);
    }

    //Usuwa relacje bike-user na podstawie id roweru
    public static void removeBikeUserRelationFromDatabase(String bikeId){
        connectToDB();
        colBike_User = db.getCollection("bike-userCollection");
        System.out.println("BikeId = "+bikeId);
        System.out.println(colBike_User.deleteOne(Filters.eq("bikeId", bikeId)));
        System.out.println(colBike_User.deleteOne(Filters.eq("bikeId", bikeId)));
    }

    public static ObjectId getUserIdFromBikeUserCollection(ObjectId bikeId){
        connectToDB();
        colBike_User = db.getCollection("bike-userCollection");
        Document document = colBike_User.find(Filters.eq("bikeId", bikeId)).first();

        return new ObjectId((String) document.get("userId"));
    }
    //endregion
}
