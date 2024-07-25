package service;

import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReservationService {
    private static final ReservationService instance = new ReservationService();
    private static final long RECOMMEND_DATE_RANGE = 7;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String UNDERSCORE = "_";
    public static ReservationService getInstance() {
        return ReservationService.instance;
    }

    private ReservationService() {
    }

    private Set<IRoom> rooms = new HashSet<>();
    private Set<Reservation> reservations = new HashSet<>();

    public Set<IRoom> getAllRooms() {
        return rooms;
    }

    public void addRoom(IRoom room) {
        rooms.add(room);
    }

    public IRoom getARoom(String roomId) {
        for (IRoom room : rooms) {
            if (roomId.equalsIgnoreCase(room.getRoomNumber())) {
                return room;
            }
        }
        return null;
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservations.add(reservation);
        return reservation;
    }

    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        List<IRoom> reservedRooms = new ArrayList<>();
        for (Reservation reservation : reservations) {
            reservedRooms.add(reservation.getRoom());
        }
        Set<IRoom> availableRoomList = new HashSet<>();
        for (IRoom room : rooms) {
            if (!reservedRooms.contains(room)) {
                availableRoomList.add(room);
                continue;
            }
            for (Reservation reservation : reservations) {
                if (reservation.getRoom().equals(room) && !isOverlap(checkInDate, checkOutDate, reservation.getCheckInDate(), reservation.getCheckOutDate())) {
                    availableRoomList.add(room);
                }
            }
        }
        return availableRoomList;
    }

    public Map<Collection<IRoom>, String> getRoomsDateMap(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> availableRooms = this.findRooms(checkInDate, checkOutDate);
        if (availableRooms.isEmpty()) {
            System.out.print("There is no available room within your inputted date range. ");
            checkInDate = Date.from(checkInDate.toInstant().plus(RECOMMEND_DATE_RANGE, ChronoUnit.DAYS));
            checkOutDate = Date.from(checkOutDate.toInstant().plus(RECOMMEND_DATE_RANGE, ChronoUnit.DAYS));
            availableRooms = this.findRooms(checkInDate, checkOutDate);
            if (availableRooms.isEmpty()) {
                System.out.println("Also no available room within 7 days extended from your inputted date range. Please try another date range instead.");
                return new HashMap<>();
            }
            System.out.println("Here are the recommended rooms for you if you reserve from " + DATE_FORMAT.format(checkInDate)
                    + " to " + DATE_FORMAT.format(checkOutDate) + ":");
        }
        else {
            System.out.println("List of rooms are available within the inputted date range: ");
        }
        System.out.println(availableRooms);
        String dateRange = DATE_FORMAT.format(checkInDate) + UNDERSCORE + DATE_FORMAT.format(checkOutDate);
        return new HashMap<>(Map.of(availableRooms, dateRange));
    }

    public Collection<Reservation> getCustomerReservation(Customer customer) {
        List<Reservation> cusReservation = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomer().equals(customer)) {
                cusReservation.add(reservation);
            }
        }
        return cusReservation;
    }

    public Set<Reservation> getAllReservation() {
        return reservations;
    }

    public void printAllReservation() {
        if (reservations.isEmpty()) {
            System.out.println("There is no reservation");
            return;
        }
        System.out.println("All reservations=" + reservations);
    }

    private boolean isOverlap(Date fromDate1, Date toDate1, Date fromDate2, Date toDate2) {
        return !(toDate1.before(fromDate2) || fromDate1.after(toDate2));
    }

}