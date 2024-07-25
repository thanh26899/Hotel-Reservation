import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenu {
    private static final String QUIT_CHOICE = "q";
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^\\w+([-+.']\\w+)*@[A-Za-z\\d]+\\.com$");
    public static final Pattern DATE_REGEX_PATTERN = Pattern.compile("^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}$");
    private static final String UNDERSCORE = "_";

    private final HotelResource hotelResource = HotelResource.getInstance();
    private final AdminResource adminResource = AdminResource.getInstance();

    private final Scanner scanner = new Scanner(System.in);

    public MainMenu() {
    }

    public void displayMainMenu() throws ParseException {
        menuLoop: while(true) {
            System.out.println("Main Menu");
            System.out.println("1. Find and reserve a room");
            System.out.println("2. See my reservation");
            System.out.println("3. Create an account");
            System.out.println("4. Admin");
            System.out.println("5. Exit");
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
                case 1 -> findAndReserveARoom();
                case 2 -> seeReservation();
                case 3 -> createAccount();
                case 4 -> new AdminMenu().displayAdminMenu();
                case 5 -> {
                    break menuLoop;
                }
                default -> System.out.println("Please just input 1-5!");
            }
        }
    }

    private void findAndReserveARoom() throws ParseException {
        final String NO_CHOICE = "N";
        final long ONE_DATE_RANGE = 1;

        String roomIdToBook;
        Date checkInDate;
        Date checkOutDate;
        Collection<IRoom> availableRooms;
        Map<Collection<IRoom>,String> roomsDateMap;
        String inputDate;
        Set<String> availableRoomIds = new HashSet<>();

        Collection<IRoom> allRooms = adminResource.getAllRooms();
        if (allRooms.isEmpty()) {
            System.out.println("There is no room available. Ask your admin to add new room.");
            return;
        }

        do {
            System.out.print("Input check in date (dd-MM-yyyy): ");
            do {
                inputDate = inputDate();
                if (inputDate.isEmpty()) {
                    return;
                }
                checkInDate = DATE_FORMAT.parse(inputDate);
                if (checkInDate.before(Date.from(Instant.now().minus(ONE_DATE_RANGE, ChronoUnit.DAYS)))) {
                    System.out.print("Check in date cannot be past date. Try again: ");
                }
            } while (checkInDate.before(Date.from(Instant.now().minus(ONE_DATE_RANGE, ChronoUnit.DAYS))));

            System.out.print("Input check out date (dd-MM-yyyy): ");
            do {
                inputDate = inputDate();
                if (inputDate.isEmpty()) {
                    return;
                }
                checkOutDate = DATE_FORMAT.parse(inputDate);
                if (checkOutDate.before(checkInDate)) {
                    System.out.print("Check out date have to be after check in date. Try again: ");
                }
            } while (checkOutDate.before(checkInDate));
            roomsDateMap = hotelResource.getRoomsDateMap(checkInDate, checkOutDate);
        } while (roomsDateMap.isEmpty());

        availableRooms = roomsDateMap.keySet().stream().findFirst().get();
        String[] dateRange = roomsDateMap.get(availableRooms).split(UNDERSCORE);
        checkInDate = DATE_FORMAT.parse(dateRange[0]);
        checkOutDate = DATE_FORMAT.parse(dateRange[1]);
        for (IRoom availableRoom : availableRooms) {
            availableRoomIds.add(availableRoom.getRoomNumber());
        }
        System.out.print("Input room ID from above list within the date range above if you want to reserve or press 'N' to get back to main menu: ");
        do {
            roomIdToBook = scanner.nextLine();
            if (!availableRoomIds.contains(roomIdToBook) && !NO_CHOICE.equalsIgnoreCase(roomIdToBook)) {
                System.out.print("Room ID not valid, pls choose from the above available rooms list: ");
            }
            if (NO_CHOICE.equalsIgnoreCase(roomIdToBook)) {
                break;
            }
        } while (!availableRoomIds.contains(roomIdToBook) && !NO_CHOICE.equalsIgnoreCase(roomIdToBook));
        if (NO_CHOICE.equalsIgnoreCase(roomIdToBook)) {
            return;
        }
        String customerEmail = inputEmail();
        if (customerEmail.isEmpty()) {
            return;
        }
        Customer customer = hotelResource.getCustomer(customerEmail);
        if (customer == null) {
            System.out.println("Customer with this email does not exist. Please create new customer first.");
            return;
        }
        for (IRoom room : availableRooms) {
            if (availableRoomIds.contains(roomIdToBook) && room.getRoomNumber().equalsIgnoreCase(roomIdToBook)) {
                hotelResource.bookARoom(customerEmail, room, checkInDate, checkOutDate);
                System.out.println("Room " + roomIdToBook + " reserved successfully!");
                return;
            }
        }

    }

    private void seeReservation() {
        String customerEmail = inputEmail();
        if (customerEmail.isEmpty()) {
            return;
        }
        Customer customer = hotelResource.getCustomer(customerEmail);
        if (customer == null) {
            System.out.println("Customer with this email does not exist. Please create new customer first.");
            return;
        }
        Collection<Reservation> rs = hotelResource.getCustomersReservation(customerEmail);
        if (rs.isEmpty()) {
            System.out.println("You have no reservation");
            return;
        }
        System.out.println("Your reservation are: ");
        for (Reservation r : rs) {
            System.out.println(r + " ");
        }
    }

    private void createAccount() {
        String firstName;
        String lastName;
        do {
            System.out.print("Input your first name: ");
            firstName = scanner.nextLine();
            if (firstName.trim().isEmpty()) {
                System.out.print("First name cannot be blank. ");
            }
        } while (firstName.trim().isEmpty());
        do {
            System.out.print("Input your last name: ");
            lastName = scanner.nextLine();
            if (lastName.trim().isEmpty()) {
                System.out.print("First name cannot be blank. ");
            }
        } while (lastName.trim().isEmpty());
        String email = inputEmail();
        if (email.isEmpty()) {
            return;
        }
        Customer customer = hotelResource.getCustomer(email);
        if (customer != null) {
            System.out.println("Customer with this email already existed.");
            return;
        }
        hotelResource.createACustomer(email, firstName, lastName);
        System.out.println(hotelResource.getCustomer(email) + " created successfully!");
    }

    private String inputEmail() {
        String customerEmail;
        do {
            System.out.print("Input your email: ");
            customerEmail = scanner.nextLine();
            if (!EMAIL_REGEX_PATTERN.matcher(customerEmail).matches()) {
                System.out.print("Invalid email, try again or 'Q' to quit. ");
            }
            if (customerEmail.equalsIgnoreCase(QUIT_CHOICE)) {
                System.out.println();
                return "";
            }
        } while (!EMAIL_REGEX_PATTERN.matcher(customerEmail).matches() || customerEmail.equalsIgnoreCase(QUIT_CHOICE));
        return customerEmail;
    }

    private String inputDate(){
        String inputDate;
        do {
            inputDate = scanner.nextLine();
            if (!DATE_REGEX_PATTERN.matcher(inputDate).matches()) {
                System.out.print("Invalid date, try again or 'Q' to quit: ");
            }
            if (inputDate.equalsIgnoreCase(QUIT_CHOICE)) {
                scanner.nextLine();
                return "";
            }
        } while (!DATE_REGEX_PATTERN.matcher(inputDate).matches() || inputDate.equalsIgnoreCase(QUIT_CHOICE));
        return inputDate;
    }
}