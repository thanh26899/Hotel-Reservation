import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenu {
    private static final String QUIT_CHOICE = "q";
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final Pattern EMAIL_REGEX_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern DATE_REGEX_PATTERN =
            Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", Pattern.CASE_INSENSITIVE);

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
                default -> {
                    System.out.println("Please just input 1-5!");

                }
            }
        }
    }

    private void findAndReserveARoom() throws ParseException {
        final String NO_CHOICE = "N";
        final long RECOMMEND_DATE_RANGE = 7;
        System.out.println("1. Find a room");
        System.out.println("2. Reserve a room");
        System.out.print("Please choose what you wanna do or press any key to get back to main menu: ");

        String roomIdToBook = "";
        Date checkInDate;
        Date checkOutDate;
        Collection<IRoom> availableRooms;
        String inputDate;
        Set<String> availableRoomIds = new HashSet<>();

        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input, pls try again to input a number");
            scanner.nextLine();
            return;
        }

        switch (choice) {
            case 1:
                Collection<IRoom> allRooms = adminResource.getAllRooms();
                if (allRooms.isEmpty()) {
                    System.out.println("There is no room available. Ask your admin to add new room.");
                    break;
                }
                System.out.print("Input check in date (dd-MM-yyyy): ");
                inputDate = inputDate();
                if (inputDate.isEmpty()) {
                    break;
                }
                checkInDate = DATE_FORMAT.parse(inputDate);
                System.out.print("Input check out date (dd-MM-yyyy): ");
                inputDate = inputDate();
                if (inputDate.isEmpty()) {
                    break;
                }
                checkOutDate = DATE_FORMAT.parse(inputDate);
                availableRooms = hotelResource.findARoom(checkInDate, checkOutDate);

                if (availableRooms.isEmpty()) {
                    System.out.print("There is no available room in your inputted date range. ");
                    Date recommendCheckInDate = java.sql.Date.valueOf(LocalDateTime.from(checkInDate.toInstant()).plusDays(RECOMMEND_DATE_RANGE).toLocalDate());
                    Date recommendCheckOutDate = java.sql.Date.valueOf(LocalDateTime.from(checkOutDate.toInstant()).plusDays(RECOMMEND_DATE_RANGE).toLocalDate());
                    availableRooms = hotelResource.findARoom(recommendCheckInDate, recommendCheckOutDate);
                    if (availableRooms.isEmpty()) {
                        System.out.println("Please try another date range instead.");
                        break;
                    }
                    System.out.println("Here are the recommended rooms for you if you reserve from " + DATE_FORMAT.format(recommendCheckInDate)
                            + " to " + DATE_FORMAT.format(recommendCheckOutDate) + " :" + availableRooms);
                } else {
                    System.out.println("List of rooms are available in inputted date range: " + availableRooms);
                }
                for (IRoom availableRoom : availableRooms) {
                    availableRoomIds.add(availableRoom.getRoomNumber());
                }
                System.out.print("Choose a room ID from above list if you want to reserve or press 'N' to get back to main menu: ");
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
                    break;
                }
            case 2:
                Collection<IRoom> roomList = adminResource.getAllRooms();
                if (roomList.isEmpty()) {
                    System.out.println("There is no room available. Ask your admin to add more room.");
                    break;
                } else {
                    String customerEmail = inputEmail();
                    if (customerEmail.isEmpty()) {
                        return;
                    }
                    Customer customer = hotelResource.getCustomer(customerEmail);
                    if (customer == null) {
                        System.out.println("Customer with this email does not exist. Please create new customer first.");
                        break;
                    }
                    System.out.print("Input check in date (dd-MM-yyyy): ");
                    inputDate = inputDate();
                    if (inputDate.isEmpty()) {
                        break;
                    }
                    checkInDate = DATE_FORMAT.parse(inputDate);
                    System.out.print("Input check out date (dd-MM-yyyy): ");
                    inputDate = inputDate();
                    if (inputDate.isEmpty()) {
                        break;
                    }
                    checkOutDate = DATE_FORMAT.parse(inputDate);
                    availableRooms = hotelResource.findARoom(checkInDate, checkOutDate);
                    for (IRoom availableRoom : availableRooms) {
                        availableRoomIds.add(availableRoom.getRoomNumber());
                    }
                    if (roomIdToBook.isEmpty()) {
                        boolean isBooked = false;
                        while (!isBooked) {
                            System.out.print("Input room ID you want to reserve: ");
                            String roomId = scanner.nextLine();
                            for (IRoom room : roomList) {
                                if (availableRoomIds.contains(roomId) && room.getRoomNumber().equalsIgnoreCase(roomId)) {
                                    hotelResource.bookARoom(customerEmail, room, checkInDate, checkOutDate);
                                    isBooked = true;
                                    System.out.println("Room " + roomId + " reserved successfully!");
                                    break;
                                }
                            }
                            if (!isBooked) {
                                System.out.println("Room ID is not available");
                            }
                        }
                    }
                    for (IRoom room : roomList) {
                        if (availableRoomIds.contains(room.getRoomNumber())) {
                            hotelResource.bookARoom(customerEmail, room, checkInDate, checkOutDate);
                            System.out.println("Room " + roomIdToBook + " reserved successfully!");
                            break;
                        }
                    }
                }
                break;
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
        System.out.print("Input your first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Input your last name: ");
        String lastName = scanner.nextLine();
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
    }

    private String inputEmail() {
        String customerEmail;
        do {
            System.out.print("Input your email: ");
            customerEmail = scanner.nextLine();
            if (!EMAIL_REGEX_PATTERN.matcher(customerEmail).matches()) {
                System.out.print("Invalid email, try again or 'Q' to quit: ");
            }
            if (customerEmail.equalsIgnoreCase(QUIT_CHOICE)) {
                scanner.nextLine();
                return "";
            }
        } while (!EMAIL_REGEX_PATTERN.matcher(customerEmail).matches());
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
        } while (!DATE_REGEX_PATTERN.matcher(inputDate).matches());
        return inputDate;
    }
}