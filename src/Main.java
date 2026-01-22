import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    // Metoda za kreiranje DataSource za povezivanje s bazom podataka
    private static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("AdventureWorksOBP"); // Promijenite naziv baze prema svojoj konfiguraciji
        ds.setUser("sa"); // Korisničko ime
        ds.setPassword("SQL"); // Lozinka
        ds.setEncrypt(false); // Ova linija može biti izostavljena
        return ds;
    }

    public static void main(String[] args) {
        DataSource dataSource = createDataSource();
        try (Connection connection = dataSource.getConnection(); Scanner scanner = new Scanner(System.in)) {
            System.out.println("Uspješno ste spojeni na bazu!");

            int choice;
            do {
                System.out.println("Izbornik:");
                System.out.println("1 – nova država");
                System.out.println("2 - izmjena postojeće države");
                System.out.println("3 - brisanje postojeće države");
                System.out.println("4 – prikaz svih država sortiranih po nazivu");
                System.out.println("5 – kraj");
                System.out.print("Odaberite opciju: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Čisti novi red

                switch (choice) {
                    case 1:
                        addDrzava(scanner, connection);
                        break;
                    case 2:
                        updateDrzava(scanner, connection);
                        break;
                    case 3:
                        deleteDrzava(scanner, connection);
                        break;
                    case 4:
                        displayDrzave(connection);
                        break;
                    case 5:
                        System.out.println("Izlaz iz aplikacije.");
                        break;
                    default:
                        System.out.println("Nepoznata opcija, molimo pokušajte ponovno.");
                }
            } while (choice != 5);
        } catch (SQLException e) {
            System.err.println("Greška pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void addDrzava(Scanner scanner, Connection connection) {
        try {
            System.out.print("Unesite naziv države: ");
            String naziv = scanner.nextLine();

            String sql = "INSERT INTO Drzava (Naziv) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, naziv);
                statement.executeUpdate();
                System.out.println("Država uspješno dodana.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateDrzava(Scanner scanner, Connection connection) {
        try {
            System.out.print("Unesite ID države za izmjenu: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Čisti novi red

            if (id <= 3) {
                System.out.println("Možete mijenjati samo države s ID-jem većim od 3.");
                return;
            }

            System.out.print("Unesite novi naziv države: ");
            String noviNaziv = scanner.nextLine();

            String sql = "UPDATE Drzava SET Naziv = ? WHERE IdDrzava = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, noviNaziv);
                statement.setInt(2, id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Država uspješno izmijenjena.");
                } else {
                    System.out.println("Država s tim ID-jem ne postoji.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void displayDrzave(Connection connection) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT IdDrzava, Naziv FROM Drzava ORDER BY Naziv")) {
            ResultSet resultSet = stmt.executeQuery();
            System.out.println("Popis država:");
            while (resultSet.next()) {
                System.out.printf("ID: %d, Naziv: %s%n", resultSet.getInt("IdDrzava"), resultSet.getString("Naziv"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void deleteDrzava(Scanner scanner, Connection connection) {
        try {
            System.out.print("Unesite ID države za brisanje: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Čisti novi red

            if (id <= 3) {
                System.out.println("Možete brisati samo države s ID-jem većim od 3.");
                return;
            }

            String sql = "DELETE FROM Drzava WHERE IdDrzava = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Država uspješno izbrisana.");
                } else {
                    System.out.println("Država s tim ID-jem ne postoji.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        }

    }
