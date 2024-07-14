import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Room;
import model.RoomType;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AdminMenu {
    private static final Pattern ROOM_ID_PATTERN = Pattern.compile("^[0-9]$");

    private final AdminResource adminResource = AdminResource.getInstance();
    private final HotelResource hotelResource = HotelResource.getInstance();
    private final Scanner scanner = new Scanner(System.in);
    public void displayAdminMenu() {
        while(true) {
            System.out.println("Admin Main Menu");
            System.out.println("1. See all Customers");
            System.out.println("2. See all Rooms");
            System.out.println("3. See all Reservations");
            System.out.println("4. Add a room");
            System.out.println("5. Back to Main Menu");
            System.out.print("Please choose what you wanna do: ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input, pls try again to input a number");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1 -> getAllCustomers();
                case 2 -> getAllRooms();
                case 3 -> getAllReservations();
                case 4 -> addARoom();
                case 5 -> {
                    return;
                }
                default -> {
                    System.out.println("Please just input 1-5!");

                }
            }
        }
    }

    private void addARoom() {
        IRoom roomSearch = null;
        String roomId;
        System.out.print("Enter room ID you want to add: ");

        do {
            roomId = scanner.nextLine();
            if (!ROOM_ID_PATTERN.matcher(roomId).matches()) {
                System.out.print("Invalid room ID, try again to input a number: ");
            } else {
                roomSearch = hotelResource.getRoom(roomId);
                if (roomSearch != null) {
                    System.out.print("Room with this ID already existed. Please enter another room ID: ");
                }
            }
        } while (!ROOM_ID_PATTERN.matcher(roomId).matches() || roomSearch != null);

        System.out.print("Add price for room " + roomId + ": ");

        double price;
        try {
            price = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input, pls try again to input a number");
            scanner.nextLine();
            return;
        }

        RoomType roomType = null;
        while (roomType == null) {
            System.out.print("Add room type for room " + roomId + " (SINGLE or DOUBLE): ");
            String roomTypeInput = scanner.nextLine();
            if (RoomType.SINGLE.toString().equalsIgnoreCase(roomTypeInput)) {
                roomType = RoomType.SINGLE;
            } else if (RoomType.DOUBLE.toString().equalsIgnoreCase(roomTypeInput)) {
                roomType = RoomType.DOUBLE;
            } else {
                System.out.println("Room type invalid");
            }
        }
        adminResource.addRoom(List.of(new Room(roomId, price, roomType)));
    }

    private void getAllReservations() {
        adminResource.displayAllReservations();
    }

    private void getAllRooms() {
        Collection<IRoom> rooms = adminResource.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("There is no room");
            return;
        }
        System.out.println(rooms);
    }

    private void getAllCustomers() {
        Collection<Customer> customers = adminResource.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("There is no customer");
            return;
        }
        System.out.println(customers);
    }
}