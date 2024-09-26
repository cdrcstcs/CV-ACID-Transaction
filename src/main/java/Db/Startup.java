package Db;
public class Startup {
    static Acid database;
    public static void main(String[] args) throws Exception {
        String dir = "";
        String dbName = "";
        database = Acid.getDatabase();
        database.run();
    }
}