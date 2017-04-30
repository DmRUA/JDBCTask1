import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * Created by DmRG on 20.03.2017.
 */
public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/apartments";
    static final String DB_USER = "admin";
    static final String DB_PASSWORD = "******";

    static Connection conn;

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            try {
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                createDB();
                while (true) {
                    System.out.println("1 : Добавить квартиру");
                    System.out.println("2 : Удалить квартиру");
                    System.out.println("3 : Изменить данные квартиры");
                    System.out.println("4 : Сделать выборку по району");
                    System.out.println("5 : Сделать выборку по цене");
                    System.out.println("6 : Вывести список всех квартир");
                    System.out.println("7 : Выход");

                    String str = reader.readLine();

                    switch (str) {
                        case "1": {
                            addApartment(reader);
                            break;
                        }
                        case "2": {
                            deleteApartment(reader);
                            break;
                        }
                        case "3": {
                            updateApartment(reader);
                            break;
                        }
                        case "4": {
                            districtSelect(reader);
                            break;
                        }
                        case "5": {
                            priceSelect(reader);
                            break;
                        }

                        case "6": {
                            infoAll();
                            break;
                        }

                        default:
                            return;
                    }
                }
            } finally {
                reader.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("CREATE TABLE IF NOT EXISTS Apartment(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district VARCHAR(20)," +
                    "adress VARCHAR(50), area DOUBLE(10,2), countRoom TINYINT UNSIGNED, price DOUBLE(10,2));");
        } finally {
            st.close();
        }

    }

    public static void addApartment(BufferedReader reader) {
        try {
            System.out.println("Введите район где находиться квартира");
            String district = reader.readLine();
            System.out.println("Введите адресс");
            String adress = reader.readLine();
            System.out.println("Введите площадь квартиры");
            double area = Double.parseDouble(reader.readLine());
            System.out.println("Введите количество комнат");
            int countRoom = Integer.parseInt(reader.readLine());
            System.out.println("Введите цену квартиры");
            int price = Integer.parseInt(reader.readLine());
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartment(district, adress, area, countRoom, price) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, district);
                ps.setString(2, adress);
                ps.setDouble(3, area);
                ps.setInt(4, countRoom);
                ps.setInt(5, price);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteApartment(BufferedReader reader) throws IOException, SQLException {
        System.out.println("Введите адресс квартиры");
        String adress = reader.readLine();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Apartment WHERE adress = ?")) {
            ps.setString(1, adress);
            ps.executeUpdate();
        }
    }

    public static void updateApartment(BufferedReader reader) throws IOException, SQLException {
        System.out.println("Введите адресс квартиры которую нужно изменить");
        String adress = reader.readLine();
        System.out.println("Введите новую цену");
        int price = Integer.parseInt(reader.readLine());
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Apartment SET price = ? WHERE adress = ?")) {
            ps.setInt(1, price);
            ps.setString(2, adress);
            ps.executeUpdate();
        }
    }

    public static void infoAll() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartment"); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t\t\t\t\t");
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    System.out.print(rs.getString(i) + "\t\t\t\t\t");
                }
                System.out.println();
            }
        }
    }

    public static void districtSelect(BufferedReader reader) throws IOException {
        System.out.println("Введите название района по которому хотите сделать выборку");
        String district = reader.readLine();
        try (PreparedStatement ps = conn.prepareStatement("SELECT adress, area, countRoom, price FROM Apartment WHERE district = ?")) {
            ps.setString(1, district);
            ResultSet rs = ps.executeQuery();
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    System.out.print(rsmd.getColumnName(i) + "\t\t\t\t\t");
                }
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                        System.out.print(rs.getString(i) + "\t\t\t\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void priceSelect(BufferedReader reader) throws IOException {
        System.out.println("Введите нижнюю границу по цене");
        int downPrice = Integer.parseInt(reader.readLine());
        System.out.println("Введите верхнюю границу по цене");
        int upPrice = Integer.parseInt(reader.readLine());
        try (PreparedStatement ps = conn.prepareStatement("SELECT district, adress, countRoom, price FROM Apartment WHERE price BETWEEN  ? AND  ? ORDER BY price DESC")) {
            ps.setInt(1, downPrice);
            ps.setInt(2, upPrice);
            ResultSet rs = ps.executeQuery();
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    System.out.print(rsmd.getColumnName(i) + "\t\t\t\t");
                }
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                        System.out.print(rs.getString(i) + "\t\t\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

